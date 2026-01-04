# Java 25 JDK base
FROM eclipse-temurin:25-jdk

ARG DEBIAN_FRONTEND=noninteractive
ARG JJAVA_VERSION=1.0-a6

# System deps: Python + venv + tooling to fetch/unzip kernelspec
RUN apt-get update && apt-get install -y --no-install-recommends \
      python3 python3-venv python3-pip \
      curl ca-certificates unzip tini \
    && rm -rf /var/lib/apt/lists/*

# Create a non-root user
RUN useradd -m -s /bin/bash jovyan
USER jovyan
WORKDIR /home/jovyan

# Create venv and install JupyterLab into it
ENV VENV_PATH=/home/jovyan/.venv
RUN python3 -m venv "$VENV_PATH" \
 && "$VENV_PATH/bin/python" -m pip install --no-cache-dir --upgrade pip \
 && "$VENV_PATH/bin/pip" install --no-cache-dir jupyterlab

# Make venv tools available on PATH
ENV PATH="$VENV_PATH/bin:${PATH}"

# Install JJava kernelspec (from GitHub release asset) and register it
RUN mkdir -p /home/jovyan/tmp/jjava \
 && curl -L -o /home/jovyan/tmp/jjava/jjava-kernelspec.zip \
      "https://github.com/dflib/jjava/releases/download/${JJAVA_VERSION}/jjava-${JJAVA_VERSION}-kernelspec.zip" \
 && unzip -q /home/jovyan/tmp/jjava/jjava-kernelspec.zip -d /home/jovyan/tmp/jjava \
 && KS_DIR="$(dirname "$(find /home/jovyan/tmp/jjava -type f -name kernel.json -print -quit)")" \
 && test -n "$KS_DIR" \
 && jupyter kernelspec install "$KS_DIR" --user --name=java \
 && rm -rf /home/jovyan/tmp/jjava


# Default working dir for notebooks
RUN mkdir -p /home/jovyan/work
WORKDIR /home/jovyan/work

EXPOSE 8888

# Optional: configure JVM opts for the kernel (override in docker-compose)
ENV JJAVA_JVM_OPTS="-Xms256m -Xmx2g"

# Use tini as PID 1
USER root
ENTRYPOINT ["/usr/bin/tini", "--"]
USER jovyan

# JupyterLab startup (no auth token/password for localhost convenience)
# with expose-app-in-browser, allowing calling from javascript the kernel 
# and passing value to static java method, see the interactive-examples.ipynb
# CMD ["bash", "-lc", "jupyter lab --ip=0.0.0.0 --port=8888 --no-browser --notebook-dir=/home/jovyan/work --expose-app-in-browser --ServerApp.token='' --ServerApp.password='' --NotebookApp.token='' --NotebookApp.password=''"]

# JupyterLab startup (no auth token/password for localhost convenience)
CMD ["bash", "-lc", "jupyter lab --ip=0.0.0.0 --port=8888 --no-browser --notebook-dir=/home/jovyan/work --ServerApp.token='' --ServerApp.password='' --NotebookApp.token='' --NotebookApp.password=''"]

