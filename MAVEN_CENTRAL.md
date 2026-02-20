# Publishing SQLGlot Java to Maven Central

This guide explains how to publish SQLGlot Java to Maven Central Repository.

## Quick Start

**For regular development:**
```bash
mvn clean install  # or mvn clean test
# No GPG required - build works as-is
```

**For Maven Central deployment:**
```bash
mvn clean deploy -P ossrh  # Activates GPG signing and OSSRH deployment
# Requires GPG keys to be set up (see Prerequisites below)
```

## Prerequisites

1. **JIRA Account**: Create an account at https://issues.sonatype.org/
2. **GitHub Account**: Repository at https://github.com/gtkcyber/sqlglot_java
3. **GPG Installed**: Install GPG for signing artifacts
4. **Maven Installed**: Maven 3.6.0 or later

## Step 1: Setup GPG Keys

### Generate GPG Key Pair

```bash
gpg --gen-key
```

Follow the prompts:
- Key type: RSA and RSA
- Key size: 4096
- Validity: 0 (does not expire) or choose a duration
- Enter your name and email

### Export Public Key

```bash
gpg --armor --export YOUR_KEY_ID > public.key
```

### Publish Public Key to Keyserver

```bash
gpg --keyserver hkp://keyserver.ubuntu.com:80 --send-keys YOUR_KEY_ID
```

You can verify your key was published by visiting:
https://keys.openpgp.org/search?q=YOUR_EMAIL

## Step 2: Configure Maven Settings

Create/edit `~/.m2/settings.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0
                              http://maven.apache.org/xsd/settings-1.1.0.xsd">

    <servers>
        <!-- OSSRH (Sonatype) credentials -->
        <server>
            <id>ossrh</id>
            <username>YOUR_JIRA_USERNAME</username>
            <password>YOUR_JIRA_PASSWORD</password>
        </server>

        <!-- GPG passphrase -->
        <server>
            <id>gpg.passphrase</id>
            <passphrase>YOUR_GPG_PASSPHRASE</passphrase>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>ossrh</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.executable>gpg</gpg.executable>
                <gpg.passphrase>YOUR_GPG_PASSPHRASE</gpg.passphrase>
            </properties>
        </profile>
    </profiles>

</settings>
```

## Step 3: Register Namespace (First Time Only)

1. Go to https://issues.sonatype.org/
2. Create a new issue:
   - Project: Community Developers (OSSRH)
   - Issue Type: New Project
   - Summary: New hosting request for com.gtkcyber.sqlglot
   - Description:
     ```
     Project: SQLGlot Java
     URL: https://github.com/gtkcyber/sqlglot_java
     License: MIT
     Group Id: com.gtkcyber.sqlglot
     SCM: https://github.com/gtkcyber/sqlglot_java.git
     ```
3. Wait for approval (usually within 1-2 business days)

## Step 4: Deploy to Maven Central

**Important:** GPG signing is only required when deploying to Maven Central. Regular builds (test, install) do NOT require GPG.

### Build and Sign Artifacts

To deploy to Maven Central, activate the `ossrh` profile which enables GPG signing:

```bash
# With passphrase on command line (not recommended for security)
mvn clean deploy -P ossrh -Dgpg.passphrase=YOUR_GPG_PASSPHRASE

# Better: Maven will prompt for GPG passphrase interactively
mvn clean deploy -P ossrh
```

This will:
1. Compile the code
2. Run all tests (283 tests)
3. Generate JAR files
4. Generate Javadoc JARs
5. Generate source JARs
6. Sign all artifacts with GPG (only when using `-P ossrh` profile)
7. Deploy to OSSRH staging repository

### Regular Builds (Without Deployment)

For regular development builds, GPG is NOT required:

```bash
# Regular build and test (no GPG needed)
mvn clean install

# Run tests only (no GPG needed)
mvn clean test

# Package only (no GPG needed)
mvn clean package
```

### Verify Deployment

Check the staging repository at:
https://s01.oss.sonatype.org/

Steps:
1. Click "Staging Repositories"
2. Find the repository starting with `comgtkcyber-`
3. Verify all artifacts are present
4. Click "Release" to promote to Maven Central

The release typically appears on Maven Central within 10 minutes, and on https://mvnrepository.com/ within a few hours.

## Step 5: Verify Publication

Verify your artifacts are available:

```bash
# Search on Maven Central
# Navigate to: https://mvnrepository.com/artifact/com.gtkcyber.sqlglot

# Or test with a local project:
mvn dependency:get -Dartifact=com.gtkcyber.sqlglot:sqlglot-core:1.0.0
```

## Usage in Other Projects

Once published, users can include SQLGlot Java in their pom.xml:

```xml
<dependency>
    <groupId>com.gtkcyber.sqlglot</groupId>
    <artifactId>sqlglot-core</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Optional: For dialect support -->
<dependency>
    <groupId>com.gtkcyber.sqlglot</groupId>
    <artifactId>sqlglot-dialects</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Troubleshooting

### GPG Signing Issues

If you get "gpg: Cannot connect to agent" error:

```bash
# Kill existing gpg-agent
killall gpg-agent

# Clear gpg cache
rm -r ~/.gnupg/*.conf-*
```

### Passphrase Not Found

If Maven doesn't prompt for passphrase:

```bash
# Configure settings.xml with gpg.passphrase property
# Or use: mvn deploy -Dgpg.passphrase=YOUR_PASSPHRASE
```

### Artifacts Not Found in Staging

1. Check that all modules compiled successfully
2. Verify javadoc generation succeeded
3. Check build logs for source jar generation
4. Ensure GPG signing completed

### Release Not Appearing

1. Verify artifacts are in staging repository
2. Click "Release" button to promote to Maven Central
3. Wait 10-30 minutes for Maven Central sync
4. Check https://repo.maven.apache.org/maven2/

## For Future Releases

To release version 1.1.0:

1. Update version in all pom.xml files:
   ```bash
   mvn versions:set -DnewVersion=1.1.0
   ```

2. Commit and tag:
   ```bash
   git add -A
   git commit -m "Bump version to 1.1.0"
   git tag -a v1.1.0 -m "Release 1.1.0"
   git push origin master --tags
   ```

3. Deploy:
   ```bash
   mvn clean deploy -P ossrh
   ```

4. Release from OSSRH staging repository

## Resources

- [OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [Sonatype Maven Guide](https://central.sonatype.org/publish/requirements/)
- [Maven GPG Plugin](https://maven.apache.org/plugins/maven-gpg-plugin/)
- [Maven Source Plugin](https://maven.apache.org/plugins/maven-source-plugin/)
- [Maven Javadoc Plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/)
