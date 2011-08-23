Heroku for Java Workbook
========================

This workbook walks you through the steps of building Java apps that can run on Heroku.

Tip: You can find the latest version of this workbook, including all the source code, online at:  
[https://github.com/heroku/java-workbook](https://github.com/heroku/java-workbook)


Prerequisites
-------------

Before you get started, you need to set up your development environment, as specified in the Prerequisites section of each
tutorial.


Terminology
-----------

We will start with some terminology that is helpful to understand.

PaaS - "Platform as a Service" is a general term for managed environments that run apps.

Heroku - A PaaS provider.

heroku - (Lower case "h") The command-line client for managing apps on Heroku.

git - A popular distributed version control system that is used to deploy apps to Heroku.

Heroku Add-on - The primary way Heroku can be extended. Add-ons are exposed as services that can be used from any app on Heroku.

Tutorial #1: Building a Web App
-------------------------------

In this tutorial, you will create a web app and deploy it to Heroku. You will use a Maven archetype to create the app, which will include a web server. You'll first run the app locally, and then deploy it to Heroku using git.

### Prerequisites

 * Create an account on [heroku.com](https://api.heroku.com/signup)
 * Install the `heroku` command-line client (Appendix A)
 * Installing git and Setting up an SSH Key (Appendix B)
 * Install Maven (Appendix C)

#### Step 1: Create a Web App

1. Using the terminal or command line, navigate to the directory where you want to create the new project (this can be in your user's home directory). In the lab where multiple people use the same computer, you should create a new sub-directory for your code.
2. Run the following Maven command to generate a new project directory containing the basic web app structure, Maven dependencies and build definitions, and a Java class that will start an embedded Jetty web process:

        mvn archetype:generate -DarchetypeGroupId=org.mortbay.jetty.archetype -DarchetypeArtifactId=jetty-archetype-assembler -DarchetypeVersion=7.5.0.RC0

    After the dependencies are downloaded you will be prompted for some properties for your project.

3. For the `groupId` enter: `foo`

4. For the `artifactId` enter: `helloheroku`

5. For the version, package, and confirmation prompts, accept the defaults by pressing `Enter`.

    A new project will be created in the `helloheroku` directory. This project contains everything needed for a Java web app.

    * The `src/main/webapp` directory contains a default `index.html` page and the typical Java web app's `WEB-INF` directory.
    * The `src/main/java/foo` directory contains a generated `Main.java` file that we will use later to start the web server process.
    * The `helloheroku` directory contains a `pom.xml` file that contains the project configuration and dependencies for Maven.

Note: Mac and Linux use forward-slashes ("/") for file paths, and Windows uses back-slashes ("\"). The workbook lists variants of commands for Mac, Linux, and Windows. However, references to file paths and sample output only use forward-slashes.

#### Step 2: Test the App Locally

1. Compile and install the app into the local Maven repository by running:

    cd helloheroku
    mvn install

    Maven creates startup scripts for the web app and installs a jar file in the local Maven repository. At the end of the output, you should see output similar to the following:

        [INFO] Installing /home/jamesw/projects/helloheroku/target/helloheroku-1.0-SNAPSHOT.jar to /home/jamesw/.m2/repository/foo/helloheroku/1.0-SNAPSHOT/helloheroku-1.0-SNAPSHOT.jar
        [INFO] Installing /home/jamesw/projects/helloheroku/pom.xml to /home/jamesw/.m2/repository/foo/helloheroku/1.0-SNAPSHOT/helloheroku-1.0-SNAPSHOT.pom
        [INFO] ------------------------------------------------------------------------
        [INFO] BUILD SUCCESS
        [INFO] ------------------------------------------------------------------------
        [INFO] Total time: 2.980s
        [INFO] Finished at: Fri Jul 22 07:30:10 MDT 2011
        [INFO] Final Memory: 11M/167M
        [INFO] ------------------------------------------------------------------------

2. Set the REPO environment variable to the location of the local Maven repository so the dependencies can be located when the web process is started:

    On Mac or Linux:

        export REPO=~/.m2/repository

    On Windows:

    set REPO=%UserProfile%\.m2\repository

3. Start the webapp process from the helloheroku directory:

    On Mac or Linux:

        sh target/bin/webapp

    On Windows:

        target\bin\webapp.bat

    The `webapp` process has output similar to the following:

        2011-07-22 07:32:37.439:INFO::jetty-7.4.4.v20110707
        2011-07-22 07:32:37.626:INFO::started o.e.j.w.WebAppContext{/,file:/home/jamesw/projects/helloheroku/src/main/webapp/}
        2011-07-22 07:32:37.699:INFO::Started SelectChannelConnector@0.0.0.0:8080 STARTING

    The Jetty web server process is now running.

4. Navigate to [http://localhost:8080/](http://localhost:8080/) in your browser.  You should see the following message:

        hello, world

5. Press `CTRL-C` to stop the process.

You are now ready to deploy this simple Java web app to Heroku.


#### Step 3: Deploy the Web App to Heroku

1. In the helloheroku project directory, create a new file named Procfile containing:

        web: sh target/bin/webapp

    Note: The file names, directories, and code are case sensitive. The Procfile file name must begin with an uppercase "P" character.

    Caution: Some text editors on Windows, such as Notepad, automatically append a .txt file extension to saved files. If that happens, you must remove the file extension.

    `Procfile` is a mechanism for declaring what commands are started when your dynos are run on the Heroku platform.  In this case, we want Heroku to run the webapp startup script for web dynos.

2. Initialize a local git repository, add the files to it, and commit them:

        git init
        git add .
        git commit -m "initial commit for helloheroku"

    Note: On Windows, you can ignore the following message when running the “git add .” command:

        warning : LF will be replaced by CRLF in .gitignore

    The commit operation has output similar to the following:

        [master (root-commit) b914eee] initial commit
        7 files changed, 165 insertions(+), 0 deletions(-)
        create mode 100644 .gitignore
        create mode 100644 Procfile
        create mode 100644 README.md
        create mode 100644 pom.xml
        create mode 100644 src/main/java/foo/Main.java
        create mode 100644 src/main/webapp/WEB-INF/web.xml
        create mode 100644 src/main/webapp/index.html

3. Create a new app provisioning stack on Heroku by using the `heroku` command-line client:

        heroku create --stack cedar

    Note: You must use the "cedar" stack when creating this new app because it’s the only Heroku stack that supports Java.

    The output looks similar to the following:

        Creating empty-winter-343... done, stack is cedar
        http://empty-winter-343.herokuapp.com/ | git@heroku.com:empty-winter-343.git
        Git remote heroku added

    Note: `empty-winter-343` is a randomly generated temporary name for the app. You can rename the app with any unique and valid name using the `heroku apps:rename` command.

    The create command outputs the web URL and git URL for this app. Since you had already created a git repository for this app, the heroku client automatically added the heroku remote repository information to the git configuration.

4. Deploy the app to Heroku:

        git push heroku master

    This command instructs `git` to push the app to the master branch on the heroku remote repository. This automatically triggers a Maven build on Heroku. When the build finishes, the output ends with something like the following:

        ----->Discovering process types
        Procfile declares types -> web
        -----> Compiled slug size is 17.0MB
        -----> Launching... done, v6
        http://empty-winter-343.herokuapp.com deployed to Heroku
        To git@heroku.com:empty-winter-343.git
        + 3bcf805...a72152c master -> master (forced update)

5. Verify that the output contains the message:

        Procfile declares types -> web

    If it doesn't, confirm that the `Procfile` is named correctly with no file extension and that it contains:

        web: sh target/bin/webapp

    If you fix `Procfile`, deploy the changes to Heroku:

        git add Procfile
        git commit -m "fixed Procfile"
        git push heroku master
        heroku scale web=1

6. Open the app in your browser using the generated app URL or by running:

        heroku open

    You should see `hello, world` on the web page.


#### Step 4: Scale the App on Heroku

By default, the app runs on one dyno. To add more dynos, use the `heroku scale` command.

1. Scale the app to two dynos:

        heroku scale web=2

2. See a list of your processes:

        heroku ps

    Tip: This command is very useful as a troubleshooting tool. For example, if your web app is not accessible, use `heroku ps` to ensure that a web process is running. If it’s not running, use `heroku scale web=1` to start the web app and use the heroku logs command to determine why there was a problem.

3. Scale back to one web dyno:

        heroku scale web=1

#### Step 5: View App Logs on Heroku

You can see everything that your app outputs to the console (STDOUT and STDERR) by running the heroku logs command.

1. To see the logs, run:

        heroku logs

2. To see log messages as they happen, use the "tail" mode:

        heroku logs -t

3. Press `CTRL-C` to stop seeing a tail of the logs.

#### Step 6: Roll Back a Release on Heroku

Whenever you deploy code, change a config variable, or add or remove an add-on resource, Heroku creates a new release and restarts your app. You will learn more about add-ons in Tutorial #4: Using a Heroku Add-on.

You can list the history of releases, and use rollbacks to revert to prior releases to back out of bad deployments or config changes.  This enables you to quickly revert to a known working state instead of creating a quick fix that might have other unforeseen effects.

1. To use the releases feature, install the `releases:basic` add-on.

        heroku addons:add releases:basic

    Note: If the output indicates that your app already has the add-on, you can ignore the message.

2. To try it out, change an environment variable for your app on Heroku:

        heroku config:add MYVAR=42

3. Now review your list of releases on Heroku:

        heroku releases

    You'll see a list of recent releases, including version number and the date of the release.

4. Roll back to the release before the MYVAR environment variable was set:

        heroku rollback

5. Verify that the MYVAR environment variable is no longer set:

        heroku config

### Summary

In this tutorial, you created a web app and deployed it to Heroku. You learned how to push apps to Heroku using `git` and how the `Procfile` declares what commands are started when dynos are run. You also learned how to list and scale the number of dynos, view logs, and roll back releases.



Next Steps
----------

If you've completed this workbook, you now have a few Java apps deployed and running on Heroku. You also know how to scale your apps and extend them with Heroku add-ons.

To continue exploring:

 * Visit http://devcenter.heroku.com/ to learn more about what you can do on the Heroku platform.
 * Visit http://addons.heroku.com/ to learn about the add-ons that enable you to easily extend your app by using other cloud services.

