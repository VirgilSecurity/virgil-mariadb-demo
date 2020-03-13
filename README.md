# Virgil PureKit MariaDB Demo
The Demo App is a simple web application that illustrates how [Virgil PureKit](https://developer.virgilsecurity.com/docs/purekit/) can be used with MariaDB to store and share data in the most secure way. The Demo App is based on use case involving a hypothetical business scenario involving a patient, physician and laboratory, and shows how distinct roles within a customer's application can be defined and used to restrict ePHI access in a HIPAA-compliant manner.

<img width="100%" src="https://cdn.virgilsecurity.com/assets/images/github/purekit_demo/purekit_mariadb.png" align="left" hspace="6" vspace="6">


Read more about demo and how it works [here](#explore-demo).

## Prerequisites
- [Docker](https://www.docker.com/)
- [Virgil Developer Account](https://dashboard.virgilsecurity.com/)

## Clone the demo

- Clone the PureKit demo application:

```bash
git clone https://github.com/VirgilSecurity/virgil-mariadb-demo.git
```

## Setup and run demo
- **Step #1.** Launch Docker
- **Step #2.** Create configuration files. Copy `env.template` file and rename to a new `env.config` file in the `server` directory.
- **Step #3.** Get Virgil PureKit credentials. Fill in the Virgil PureKit values inside of `env.config` file. The following environment variables must be defined to run the server:

| Variable Name | Description |
| ------------- | ------------ |
| `virgil_appId` | Your Virgil Application ID. Can be found in your [Virgil dashboard](https://dashboard.virgilsecurity.com/). |
| `virgil_at` | Your Virgil Application token.  Can be found in your [Virgil dashboard](https://dashboard.virgilsecurity.com/). |
| `virgil_pk` | PHE Service public key of your PureKit application. Can be found in your PureKit application at [Virgil dashboard](https://dashboard.virgilsecurity.com/) |
| `virgil_nms` | Your PureKit Nonrotatable master secret. |
| `virgil_bu` | Your PureKit Backup public key |
| `virgil_sk` | Your PureKit App secret key |

To generate `virgil_nms`, `virgil_bu` and `virgil_sk`, install the [Virgil CLI](https://developer.virgilsecurity.com/docs/platform/cli/install/) and then run the following command:

```bash
virgil pure keygen all
```

Make sure to save the generated keys in a safe storage.

- **Step #4.** To run the demo client use the following command:

```bash
docker-compose up
```
- **Step #5.** Browse to http://localhost:80 to explore the demo.

## Explore demo

The Demo App consists of three cards (actors): Patient (Alice), Physician (Bob) and Lab.

> At any moment you can restart the demo and check the database using the buttons in the top.

### Actors

In the beginning of the demo flow, each actor the following:

Alice has:
- her SSN, which she can share with Bob;
- access to the 2 prescriptions, that Bob has provided;
- 2 lab tests that she is waiting for.

Bob has:
- his license number, which he can share with Alice;
- 2 prescriptions that he wrote for Alice, and a button to add new prescriptions;
- 2 lab tests that he is waiting for, and a button to add a new lab test.

Lab has:
- 2 lab tests that Bob has created for Alice.

### Usage

At the Patient card:
- click "share to Bob" to securely share the Alice's social security number with the Physician.
- Also, you can review prescription, lab's results and its status.

At the Physician card:
- click "Add prescription" to create a new prescription for Alice. Alice will immediately get access to the prescription and will be able to see the notes.
- Also, at the Physician card, click "Add lab test". This will create a new record in Alice's, Bob's and Lab's "Lab tests" section.

At the Lab card:
- in the column "Results" click "Add" button to add a test result. After submitting it, the Physician will be able to see the result, but the Patient will only see "Access denied" message in the "Result" column.
- To share the result with the Patient, at the Physician card click "Share". After that the Patient will be able to read the result herself.

### View MariaDB records

Now, to see the results of your actions in the database:
- click "View MariaDB" in the top of the page. There, in the left field, scroll to the very bottom to see the PureKit-related tables.
- Click on any table and "Execute" to see the data in that table.
- Note that most of the data will be encrypted (results of the lab tests in `lab_tests`, Patient's SSN in `patients`, Physician's license number in `physycians`, notes in `prescriptions`, encrypted keys and etc.) and can only be decrypted by users who have the right keys to do so. Therefore, sensitive data cannot be accessed by unauthorized parties.

## Build docker images (optional)

Prerequisites
- [Java 11+](https://jdk.java.net/)
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com/)

To re-build docker images for demo's client and server use the steps below:
- **Step #1.** Build Java application. Navigate to the `server` directory of the PureKit Demo and execute the following command:

```bash
mvn clean package
```

- **Step #2.** Build Docker image. Execute the next command in `server` directory:

```bash
docker build -t virgilsecurity/mariadb-demo-server .
```

- **Step #3.** Build docker image for the client, navigate to the `client` directory and execute the next command:

```bash
docker build -t virgilsecurity/mariadb-demo-client .
```

## License

This Demo is released under the [3-clause BSD License](LICENSE).

## Support
Our developer support team is here to help you.

You can find us on [Twitter](https://twitter.com/VirgilSecurity) or send us email support@VirgilSecurity.com.

Also, get extra help from our support team on [Slack](https://virgilsecurity.com/join-community).
