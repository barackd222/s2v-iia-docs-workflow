# README #

Welcome to "The First" in this series of micro-apps.

## What's This All About? ##

This series is to provide some examples of using Oracle Platform Cloud Services in a *different* way and not the way that is traditionally positioned. The purpose of this series is to:

* Have a focus on Oracle Platform Cloud Services.
* Show a solution that could have a specific application - business or technical.
* Each solution is easy to put together.
* We'll *try* and deliver these regularly and often.
* The focus is about the *art of the possibility* and not *art of the purity*.
* Anyone that has a passion, can contribute to this series - not just me, not just OFM pre-sales, not just Oracle partners, anyone !!!

## What's This Isn't About? ##

We are not intending this to be:

* A best-practice approach to how products are used.
* Architecturally sound practices.
* Aligned with Oracle Statement of Direction.
* Just showing Oracle technologies working with Oracle technologies. Heterogeneous environments are encouraged. 

## What is in this repository? ##

**Name:** DoCS Routing

**Description:**

A basic document routing application. The application enables someone to upload and route documents (whilst providing them a message of what needs to be done). In addition, someone can see what has been shared.

**Technology:**

* Oracle Documents Cloud Service
* Netbeans IDE (Used 8.0.2)
* Maven (3.0)
* Apache Tomcat Server / Glassfish Application Server (as included in Netbeans)
* Jersey REST API (2.17)
* AngularJS (1.3.17)

**Inclusions:**

* A Netbeans project with the source.
* A distribution that can be deployed to Apache Tomcat.

**Exclusions:**

* An Oracle Documents Cloud Service instance
* Netbeans software
* Dependencies can be downloaded from Maven Repository

**Getting Started:**

* Get access to an Oracle Document Cloud Service.
* Create a single user to be the application user where the files are *shared*.
* As this user, create directories of the different "user names" to share. The "user names" are based upon the users email address (without the domain). eg. "jason.lowe".
* Download the project (via git or directly from the repository)
* Import the project
* Configure the project to run on Apache Tomcat or Glassfish Application Server
* Update the "config.properties" under the resources directory (in the /org/oracle/samples/docsrouting/resources directory). The host property needs the hostname to be updated. The auth property needs the Base64 version of credentials to be updated. The credentials in text is in the format of "username:password" which is then converted to Base64. Search for a base64 encoder and put the result in the auth property.
* "Run" the project.
* To "route" a document, fill in the "To" (the field value maps to the user names with directories created), "Title" and "Message" fields. Then open up an Explorer window and drag a file into the "drag-box". This will post the form data. A status will appear at the bottom of the form. A share message via an email notification will be sent to the user in the "To" field.
* To "view" shared documents, fill in the "username" field and click the "Refresh" button. This will populate the table of the shared files. You can click the link to download the files.

**Images:**

This is what it looks like:

![first-screenshot-medium.png](https://bitbucket.org/repo/9yz6bn/images/914045037-first-screenshot-medium.png)

This is what the email looks like:

![email-image-001.png](https://bitbucket.org/repo/9yz6bn/images/1169108039-email-image-001.png)

This is what the solution looks like:

![quick-arch-medium.png](https://bitbucket.org/repo/9yz6bn/images/4179569809-quick-arch-medium.png)