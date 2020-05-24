## Overview
This is the Ant build system for all AISummarizer software.  This build process
integrates Git, JUnit testing, yGuard obfuscation, encryption, and deployment
to both development and production environments.

At least Ant version 1.8.0 is recommended due to bugs in previous versions that
cause the uptodate task to not correctly detect updates.  This bug greatly
increases the partial build times.

To develop this application, you may use any IDE or text editor to modify the
source code and compile.  The application may optionally be executed in an
unobfuscated form within and IDE using the licenses generated during the
"ant test" phase.

Once the IDE built-in compiler successfully builds the project and all
dependencies, the next step is to use Ant and this build.xml to perform the
necessary obfuscation, encryption, packaging, and deployment.

The build classes are used for building, encrypting, packaging, and deploying
the systm only.  These classes should not be deployed with the system.  This is
important because they include much of the tools that would aid someone in
reverse-engineering the software.

## Procedure to publish a new release of the software
1. Update build number in  
   `../summarizer-webservices-database/src/main/resources/com/essentialmining/versions.properties`

2. Add to `ReleaseNotes.txt`

3. Update dev database

4. `ant clean`

5. `ant deploy.dev`

6. Test on dev, including download and installation of personal and enterprise editions

7. Commit to Git: status, commit, tag, push, and merge release branch (TODO: Document)

    1. `ant cvs.diff`
    2. `cvs ...`
    3. `ant cvs.tag -Dcvs.tag.tag="build_${build.number}"`

8. Update production database

9. `ant deploy.production`

10. Test production, including download and installation of personal and enterprise editions

11. Set build number to "0" for development mode in  
    `../summarizer-webservices-database/src/main/resources/com/essentialmining/versions.properties`

12. Commit/push to Git on master branch

## Contact Us
For questions or support