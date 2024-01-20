FROM openjdk:17-jdk

ENV GRADLE_HOME /opt/gradle
ENV GRADLE_VERSION 8.3

RUN set -o errexit -o nounset \
	&& echo "Downloading Gradle" \
	&& wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
	\
	&& echo "Installing Gradle" \
	&& unzip gradle.zip \
	&& rm gradle.zip \
	&& mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
	&& ln --symbolic "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle \
	\
    && echo "Adding gradle user and group" \
    && groupadd --system --gid 1000 gradle \
    && useradd --system --gid gradle --uid 1000 --shell /bin/bash --create-home gradle \
    && mkdir /home/gradle/.gradle \
    && chown --recursive gradle:gradle /home/gradle \
    \
    && echo "Symlinking root Gradle cache to gradle Gradle cache" \
    && ln -s /home/gradle/.gradle /root/.gradle

USER gradle
WORKDIR /home/gradle

ENV ANDROID_HOME="/home/gradle/android-sdk-linux"

RUN mkdir "$ANDROID_HOME" .android \
    && cd "$ANDROID_HOME" \
    && curl -o sdk.zip https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip \
    && unzip sdk.zip \
    && rm sdk.zip \
    && yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses

ENV PATH="/home/gradle/gradle/bin:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/tools/bin:${PATH}"

RUN sdkmanager 'platform-tools'
RUN sdkmanager 'platforms;android-34'
RUN sdkmanager 'build-tools;34.0.0'
RUN sdkmanager 'extras;google;m2repository'
RUN sdkmanager 'extras;android;m2repository'
RUN sdkmanager 'extras;google;google_play_services'

COPY . /app
WORKDIR /app
USER root
