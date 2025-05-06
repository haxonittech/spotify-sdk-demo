#!/bin/bash

echo "=== Building Spotify Normal App with direct Gradle download ==="

# Check for Java 11
if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
    echo "Using Java from $JAVA_HOME"
    
    if [ "$java_version" -eq 11 ]; then
        echo "✓ Java 11 is already installed"
    else
        echo "⚠️ Warning: Java version is not 11, this might cause build issues"
        export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
        echo "Set JAVA_HOME to $JAVA_HOME"
    fi
else
    echo "❌ Java is not installed. Please install Java 11."
    exit 1
fi

# Check if Gradle is already downloaded
GRADLE_VERSION="7.0.2"
GRADLE_DIR="/home/kali/gradle-${GRADLE_VERSION}"

if [ ! -d "$GRADLE_DIR" ]; then
    echo "Downloading Gradle ${GRADLE_VERSION}..."
    wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O /tmp/gradle.zip
    unzip -q /tmp/gradle.zip -d /home/kali/
    rm /tmp/gradle.zip
fi

echo "Using Gradle from $GRADLE_DIR/bin/gradle"

# Accept Android SDK licenses
echo "Accepting Android SDK licenses..."
yes | /home/kali/Desktop/android-sdk/tools/bin/sdkmanager --licenses > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✓ Licenses accepted"
else
    mkdir -p /home/kali/.android
    touch /home/kali/.android/repositories.cfg
    echo "✓ Created repositories.cfg"
fi

# Copy the Spotify SDK to the libs folder
mkdir -p app/libs
if [ ! -f "app/libs/spotify-auth-release-2.1.0.aar" ]; then
    echo "Copying Spotify SDK to libs folder..."
    cp libs/spotify-auth-release-2.1.0.aar app/libs/
fi

# Build the app
echo "Building the app with Gradle..."
$GRADLE_DIR/bin/gradle wrapper
$GRADLE_DIR/bin/gradle assembleDebug

if [ $? -eq 0 ]; then
    echo "Build successful! APK is at: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "To install the app on a connected device:"
    echo "adb install -r app/build/outputs/apk/debug/app-debug.apk"
else
    echo "Build failed."
fi
