pipeline {

    agent any

    // ── Tools (must exist in Jenkins Global Tool Config) ─────────
    tools {
        maven 'maven3'
        jdk   'jdk17'
    }

    // ── Environment ───────────────────────────────────────────────
    environment {

        // Docker Hub
        DOCKER_HUB_USER = 'ujjawalboss'
        BACKEND_IMAGE   = "${DOCKER_HUB_USER}/fashora-backend"
        FRONTEND_IMAGE  = "${DOCKER_HUB_USER}/fashora-frontend"

        // GitOps repo
        GITOPS_REPO_URL    = 'https://github.com/your-org/fashora-gitops.git'
        GITOPS_REPO_BRANCH = 'main'

        // Credentials
        DOCKERHUB_CREDS = 'dockerhub-credentials'
        GITOPS_CREDS    = 'gitops-repo-credentials'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
    }

    triggers {
        githubPush()
    }

    stages {

        // ── Checkout ───────────────────────────────────────────────
        stage('Checkout') {
            steps {
                checkout scm

                script {

                    // SAFE BRANCH DETECTION (fixes null issue)
                    def branch = env.GIT_BRANCH

                    if (!branch) {
                        branch = sh(
                            script: "git symbolic-ref --short -q HEAD || echo main",
                            returnStdout: true
                        ).trim()
                    }

                    branch = branch.replace("origin/", "")

                    // Commit SHA
                    def sha = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()

                    env.BRANCH_NAME = branch
                    env.GIT_SHA     = sha
                    env.IMAGE_TAG   = "${branch}-${sha}"

                    echo "📦 Branch: ${env.BRANCH_NAME}"
                    echo "🔖 Image Tag: ${env.IMAGE_TAG}"
                }
            }
        }

        // ── Unit Tests ─────────────────────────────────────────────
        stage('Unit Tests') {
            steps {
                dir('backend') {
                    sh 'mvn test --no-transfer-progress'
                }
            }
            post {
                always {
                    junit testResults: 'backend/target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
            }
        }

        // ── Build ──────────────────────────────────────────────────
        stage('Build JAR') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests --no-transfer-progress'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ── Code Quality (optional) ────────────────────────────────
        stage('Code Quality') {
            when {
                expression { env.SONAR_HOST_URL?.trim() }
            }
            steps {
                dir('backend') {
                    withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                        sh """
                            mvn sonar:sonar \
                              -Dsonar.projectKey=fashora-backend \
                              -Dsonar.host.url=${SONAR_HOST_URL} \
                              -Dsonar.login=${SONAR_TOKEN}
                        """
                    }
                }
            }
        }

        // ── Docker Build ───────────────────────────────────────────
        stage('Docker Build') {
            steps {
                sh """
                    docker build -t ${BACKEND_IMAGE}:${IMAGE_TAG} ./backend
                    docker build -t ${FRONTEND_IMAGE}:${IMAGE_TAG} ./frontend

                    docker tag ${BACKEND_IMAGE}:${IMAGE_TAG} ${BACKEND_IMAGE}:latest
                    docker tag ${FRONTEND_IMAGE}:${IMAGE_TAG} ${FRONTEND_IMAGE}:latest
                """
            }
        }

        // ── Docker Push ────────────────────────────────────────────
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKERHUB_CREDS}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {

                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        docker push ${BACKEND_IMAGE}:${IMAGE_TAG}
                        docker push ${BACKEND_IMAGE}:latest

                        docker push ${FRONTEND_IMAGE}:${IMAGE_TAG}
                        docker push ${FRONTEND_IMAGE}:latest

                        docker logout
                    """
                }
            }
        }

        // ── GitOps Update ──────────────────────────────────────────
        stage('Update GitOps Repo') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${GITOPS_CREDS}",
                    usernameVariable: 'GIT_USER',
                    passwordVariable: 'GIT_PASS'
                )]) {

                    sh """
                        rm -rf gitops
                        git clone https://${GIT_USER}:${GIT_PASS}@\$(echo ${GITOPS_REPO_URL} | sed 's|https://||') gitops
                        cd gitops

                        OVERLAY="dev"
                        if [ "${env.BRANCH_NAME}" = "main" ]; then OVERLAY="prod"; fi
                        if [ "${env.BRANCH_NAME}" = "develop" ]; then OVERLAY="staging"; fi

                        sed -i "s|newTag:.*# backend|newTag: ${IMAGE_TAG} # backend|g" overlays/\$OVERLAY/kustomization.yaml
                        sed -i "s|newTag:.*# frontend|newTag: ${IMAGE_TAG} # frontend|g" overlays/\$OVERLAY/kustomization.yaml

                        git config user.email "jenkins@ci.com"
                        git config user.name "Jenkins CI"

                        git add overlays/\$OVERLAY/kustomization.yaml
                        git commit -m "ci: update image ${IMAGE_TAG}" || true
                        git push origin ${GITOPS_REPO_BRANCH}
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ PIPELINE SUCCESS | ${env.IMAGE_TAG}"
        }

        failure {
            echo "❌ PIPELINE FAILED"
        }

        always {
            cleanWs()
        }
    }
}