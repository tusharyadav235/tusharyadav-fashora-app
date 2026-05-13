// ════════════════════════════════════════════════════════════════════════════
//  Fashora — Jenkins Declarative Pipeline
//
//  Stages:
//   1. Checkout          — clone app repo
//   2. Unit Tests        — Maven test (backend)
//   3. Build JAR         — Maven package
//   4. Code Quality      — SonarQube scan (optional, skipped if no server)
//   5. Docker Build      — build backend + frontend images
//   6. Docker Push       — push to Docker Hub with SHA + branch tags
//   7. Update GitOps     — patch image tags in k8s manifests repo & push
//   8. Notify            — Slack / email on success or failure
//
//  Required Jenkins credentials:
//   • dockerhub-credentials  (Username/Password)
//   • gitops-repo-credentials (Username/Password or SSH)
//   • sonarqube-token        (Secret text, optional)
//   • slack-webhook          (Secret text, optional)
// ════════════════════════════════════════════════════════════════════════════

pipeline {

    agent any

    // ── Tool versions (configure these in Jenkins → Global Tool Config) ──────
    tools {
        maven 'Maven-3.9'
        jdk   'JDK-17'
    }

    // ── Environment variables ────────────────────────────────────────────────
    environment {
        // Docker Hub
        DOCKER_HUB_USER    = 'your-dockerhub-username'          // ← change
        BACKEND_IMAGE      = "${DOCKER_HUB_USER}/fashora-backend"
        FRONTEND_IMAGE     = "${DOCKER_HUB_USER}/fashora-frontend"

        // GitOps repo (separate repo that ArgoCD watches)
        GITOPS_REPO_URL    = 'https://github.com/your-org/fashora-gitops.git' // ← change
        GITOPS_REPO_BRANCH = 'main'

        // Image tag strategy: git short SHA + branch
        GIT_SHA            = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        IMAGE_TAG          = "${env.BRANCH_NAME}-${env.GIT_SHA}"

        // Credential IDs stored in Jenkins
        DOCKERHUB_CREDS    = 'dockerhub-credentials'
        GITOPS_CREDS       = 'gitops-repo-credentials'
    }

    // ── Pipeline options ─────────────────────────────────────────────────────
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
    }

    // ── Trigger: build on every push ─────────────────────────────────────────
    triggers {
        githubPush()
    }

    // ════════════════════════════════════════════════════════════════════════
    stages {

        // ── Stage 1: Checkout ────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "📥 Checking out branch: ${env.BRANCH_NAME} @ ${env.GIT_SHA}"
                checkout scm
            }
        }

        // ── Stage 2: Unit Tests ──────────────────────────────────────────────
        stage('Unit Tests') {
            steps {
                dir('backend') {
                    echo "🧪 Running unit tests..."
                    sh 'mvn test -pl . --no-transfer-progress'
                }
            }
            post {
                always {
                    junit testResults: 'backend/target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
                failure {
                    echo "❌ Unit tests failed. Aborting pipeline."
                }
            }
        }

        // ── Stage 3: Build JAR ───────────────────────────────────────────────
        stage('Build JAR') {
            steps {
                dir('backend') {
                    echo "🔨 Building Spring Boot JAR..."
                    sh 'mvn clean package -DskipTests --no-transfer-progress'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ── Stage 4: Code Quality (SonarQube) ───────────────────────────────
        stage('Code Quality') {
            when {
                // Only run if SONAR_HOST_URL env var is set in Jenkins
                expression { env.SONAR_HOST_URL?.trim() }
            }
            steps {
                dir('backend') {
                    withCredentials([string(credentialsId: 'sonarqube-token',
                                           variable: 'SONAR_TOKEN')]) {
                        echo "🔍 Running SonarQube analysis..."
                        sh """
                            mvn sonar:sonar \
                              -Dsonar.projectKey=fashora-backend \
                              -Dsonar.host.url=${SONAR_HOST_URL} \
                              -Dsonar.login=${SONAR_TOKEN} \
                              --no-transfer-progress
                        """
                    }
                }
            }
        }

        // ── Stage 5: Docker Build ────────────────────────────────────────────
        stage('Docker Build') {
            steps {
                echo "🐳 Building Docker images with tag: ${IMAGE_TAG}"

                // Backend
                sh """
                    docker build \
                      --build-arg BUILD_DATE=\$(date -u +"%Y-%m-%dT%H:%M:%SZ") \
                      --build-arg GIT_SHA=${GIT_SHA} \
                      -t ${BACKEND_IMAGE}:${IMAGE_TAG} \
                      -t ${BACKEND_IMAGE}:latest \
                      ./backend
                """

                // Frontend
                sh """
                    docker build \
                      -t ${FRONTEND_IMAGE}:${IMAGE_TAG} \
                      -t ${FRONTEND_IMAGE}:latest \
                      ./frontend
                """

                echo "✅ Docker images built successfully"
            }
        }

        // ── Stage 6: Docker Push ─────────────────────────────────────────────
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                        credentialsId: "${DOCKERHUB_CREDS}",
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS')]) {

                    echo "📤 Pushing images to Docker Hub..."
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'

                    sh "docker push ${BACKEND_IMAGE}:${IMAGE_TAG}"
                    sh "docker push ${BACKEND_IMAGE}:latest"
                    sh "docker push ${FRONTEND_IMAGE}:${IMAGE_TAG}"
                    sh "docker push ${FRONTEND_IMAGE}:latest"

                    sh 'docker logout'
                    echo "✅ Images pushed: ${IMAGE_TAG}"
                }
            }
        }

        // ── Stage 7: Update GitOps Repo ──────────────────────────────────────
        stage('Update GitOps Repo') {
            steps {
                withCredentials([usernamePassword(
                        credentialsId: "${GITOPS_CREDS}",
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_PASS')]) {

                    echo "📝 Updating Kubernetes manifests in GitOps repo..."

                    sh """
                        # Clone the GitOps repo
                        rm -rf gitops-tmp
                        git clone https://${GIT_USER}:${GIT_PASS}@\$(echo ${GITOPS_REPO_URL} | sed 's|https://||') gitops-tmp
                        cd gitops-tmp

                        # Determine overlay (main→prod, develop→staging, else→dev)
                        OVERLAY="dev"
                        if [ "${env.BRANCH_NAME}" = "main" ];    then OVERLAY="prod";    fi
                        if [ "${env.BRANCH_NAME}" = "develop" ]; then OVERLAY="staging"; fi

                        echo "Deploying to overlay: \$OVERLAY"

                        # Patch backend image tag in kustomization.yaml
                        sed -i "s|newTag:.*# backend|newTag: ${IMAGE_TAG} # backend|g" \
                            overlays/\$OVERLAY/kustomization.yaml

                        # Patch frontend image tag in kustomization.yaml
                        sed -i "s|newTag:.*# frontend|newTag: ${IMAGE_TAG} # frontend|g" \
                            overlays/\$OVERLAY/kustomization.yaml

                        # Commit and push
                        git config user.email "jenkins@fashora.com"
                        git config user.name  "Jenkins CI"
                        git add overlays/\$OVERLAY/kustomization.yaml
                        git commit -m "ci: update image tags to ${IMAGE_TAG} [skip ci]"
                        git push origin ${GITOPS_REPO_BRANCH}

                        echo "✅ GitOps repo updated — ArgoCD will sync automatically"
                    """
                }
            }
            post {
                always {
                    sh 'rm -rf gitops-tmp'
                }
            }
        }

    } // end stages

    // ════════════════════════════════════════════════════════════════════════
    post {

        success {
            echo """
            ╔══════════════════════════════════════╗
            ║  ✅  PIPELINE SUCCEEDED              ║
            ║  Branch : ${env.BRANCH_NAME}
            ║  Tag    : ${IMAGE_TAG}
            ║  ArgoCD will deploy automatically   ║
            ╚══════════════════════════════════════╝
            """
            // Optional Slack notification
            script {
                if (env.SLACK_WEBHOOK) {
                    sh """
                        curl -X POST -H 'Content-type: application/json' \
                          --data '{"text":"✅ *Fashora* pipeline succeeded\\nBranch: ${env.BRANCH_NAME}\\nTag: ${IMAGE_TAG}"}' \
                          ${env.SLACK_WEBHOOK}
                    """
                }
            }
        }

        failure {
            echo "❌ Pipeline FAILED on branch ${env.BRANCH_NAME}"
            script {
                if (env.SLACK_WEBHOOK) {
                    sh """
                        curl -X POST -H 'Content-type: application/json' \
                          --data '{"text":"❌ *Fashora* pipeline FAILED\\nBranch: ${env.BRANCH_NAME}\\nCheck: ${env.BUILD_URL}"}' \
                          ${env.SLACK_WEBHOOK}
                    """
                }
            }
        }

        always {
            // Clean up local Docker images to save disk space on Jenkins agent
            sh """
                docker rmi ${BACKEND_IMAGE}:${IMAGE_TAG}  || true
                docker rmi ${FRONTEND_IMAGE}:${IMAGE_TAG} || true
            """
            cleanWs()
        }
    }
}
