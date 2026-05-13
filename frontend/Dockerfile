# ══════════════════════════════════════════════════════════════════════════════
#  Fashora Frontend — Nginx static server
#  Supports linux/amd64 and linux/arm64
# ══════════════════════════════════════════════════════════════════════════════
FROM nginx:stable

LABEL maintainer="fashora-team"
LABEL app="fashora-frontend"

RUN rm /etc/nginx/conf.d/default.conf

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY index.html /usr/share/nginx/html/index.html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -fs http://localhost:80/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
