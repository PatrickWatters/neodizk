<h1 align="center">DIZK</h1>
<p align="center">
    <a href="https://travis-ci.org/scipr-lab/dizk"><img src="https://travis-ci.org/scipr-lab/dizk.svg?branch=master"></a>
    <a href="https://github.com/scipr-lab/dizk/blob/master/AUTHORS"><img src="https://img.shields.io/badge/authors-SCIPR%20Lab-orange.svg"></a>
    <a href="https://github.com/scipr-lab/dizk/blob/master/LICENSE"><img src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
</p>
<h4 align="center">Java library for DIstributed Zero Knowledge proof systems</h4>

___DIZK___ (pronounced */'dizək/*) is a Java library for distributed zero knowledge proof systems. The library implements distributed polynomial evaluation/interpolation, computation of Lagrange polynomials, and multi-scalar multiplication. Using these scalable arithmetic subroutines, the library provides a distributed zkSNARK proof system that enables verifiable computations of up to billions of logical gates, far exceeding the scale of previous state-of-the-art solutions.

The library is developed by [SCIPR Lab](http://www.scipr-lab.org/) and contributors (see [AUTHORS](AUTHORS) file) and is released under the MIT License (see [LICENSE](LICENSE) file).

The library is developed as part of a paper called *"[DIZK: A Distributed Zero Knowledge Proof System](https://eprint.iacr.org/2018/691)"*.

**WARNING:** This is an academic proof-of-concept prototype. This implementation is not ready for production use. It does not yet contain all the features, careful code review, tests, and integration that are needed for a deployment!

## Table of contents

- [Directory structure](#directory-structure)
- [Overview](#overview)
- [Build guide](#build-guide)
- [Configuring AWS and using Flintrock to manage a testing cluster](#configuring-aws-and-using-flintrock-to-manage-a-testing-cluster)
- [Benchmarks](#benchmarks)
- [References](#references)
- [License](#license)

## Directory structure

The directory structure is as follows:

* [__src__](src): Java directory for source code and unit tests
  * [__main/java__](src/main/java): Java source code, containing the following modules:
    * [__algebra__](src/main/java/algebra): fields, groups, elliptic curves, FFT, multi-scalar multiplication
    * [__bace__](src/main/java/bace): batch arithmetic circuit evaluation
    * [__common__](src/main/java/common): standard arithmetic and Spark computation utilities
    * [__configuration__](src/main/java/configuration): configuration settings for the Spark cluster
    * [__profiler__](src/main/java/profiler): profiling infrastructure for zero-knowledge proof systems
    * [__reductions__](src/main/java/reductions): reductions between languages (used internally)
    * [__relations__](src/main/java/relations): interfaces for expressing statement (relations between instances and witnesses) as various NP-complete languages
    * [__zk_proof_systems__](src/main/java/zk_proof_systems): serial and distributed implementations of zero-knowledge proof systems
  * [__test/java__](src/test/java): Java unit tests for the provided modules and infrastructure

## Overview

This library implements a distributed zero knowledge proof system, enabling scalably proving (and verifying) the integrity of computations, in zero knowledge.

A prover who knows the witness for an NP statement (i.e., a satisfying input/assignment) can produce a short proof attesting to the truth of the NP statement. This proof can then be verified by anyone, and offers the following properties.

- **Zero knowledge** - the verifier learns nothing from the proof besides the truth of the statement.
- **Succinctness** - the proof is small in size and cheap to verify.
- **Non-interactivity** - the proof does not require back-and-forth interaction between the prover and the verifier.
- **Soundness** - the proof is computationally sound (such a proof is called an *argument*).
- **Proof of knowledge** - the proof attests not just that the NP statement is true, but also that the prover knows why.

These properties comprise a **zkSNARK**, which stands for *Zero-Knowledge Succinct Non-interactive ARgument of Knowledge*.
For formal definitions and theoretical discussions about these, see [BCCT12] [BCIOP13] and the references therein.

**DIZK** provides Java-based implementations using Apache Spark [Apa17] for:

1. Proof systems
    - A serial and distributed preprocessing zkSNARK for *R1CS* (Rank-1 Constraint Systems), an NP-complete language that resembles arithmetic circuit satisfiability. The zkSNARK is the protocol in [Gro16].
    - A distributed Merlin-Arthur proof system for evaluating arithmetic circuits on batches of inputs; see [Wil16].
2. Scalable arithmetic
    - A serial and distributed radix-2 fast Fourier transform (FFT); see [Sze11].
    - A serial and distributed multi-scalar multiplication (MSM); see [BGMW93] [Pip76] [Pip80].
    - A serial and distributed Lagrange interpolation (Lag); see [BT04].
3. Applications using the above zkSNARK for
    - Authenticity of photos on three transformations (crop, rotation, blur); see [NT16].
    - Integrity of machine learning models with support for linear regression and covariance matrices; see [Bis06] [Can69] [LRF97] [vW97].

## Build guide

The library has the following dependencies:

- Java 11
- [Apache Maven](https://maven.apache.org/) (see [here](https://maven.apache.org/guides/mini/guide-configuring-maven.html) for configuration guide)
- Fetched from `pom.xml` via Maven:
    - [Spark Core 3.0.1](https://mvnrepository.com/artifact/org.apache.spark/spark-core)
    - [Spark SQL 3.0.1](https://mvnrepository.com/artifact/org.apache.spark/spark-sql)
    - [JUnit 5.7.0](https://mvnrepository.com/artifact/org.junit.jupiter)
    - [Spotless with Google Java Format](https://github.com/diffplug/spotless/tree/main/plugin-maven#google-java-format)

More information about compilation options can be found [here](http://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html)

### Why Java?

This library uses Apache Spark, an open-source cluster-computing framework that natively supports Java, Scala, and Python. Among these, we found Java to fit our goals because we could leverage its rich features for object-oriented programming and we could control execution in a (relatively) fine-grained way.

While other libraries for zero knowledge proof systems are written in low-level languages (e.g., [libsnark](https://github.com/scipr-lab/libsnark) is written in C++ and [bellman](https://github.com/zkcrypto/bellman) in Rust), harnessing the speed of such languages in our setting is not straightforward. For example, we evaluated the possibility of interfacing with C (using native binding approaches like JNI and JNA), and concluded that the cost of memory management and process inferfacing resulted in a slower performance than from purely native Java execution.

### Installation

Start by cloning this repository and entering the repository working directory:
```bash
git clone https://github.com/clearmatics/neodizk.git
cd neodizk
# Set up your environment
. ./setup_env
```

Finally, compile the source code:
```bash
mvn compile
```

### Docker

```bash
docker build -t neodizk-base .
docker run -it --name neodizk-container neodizk-base
```

**Note**: For development purpose, you may want to develop from inside a docker container (to avoid touching your local system's configuration). To do so, you can run the following command:
```bash
docker run -ti -v "$(pwd)":/home/dizk-dev neodizk-base
```
and run `cd /home/dizk-dev` in the container to start developing.

### Testing

This library comes with unit tests for each of the provided modules. Run the tests with:
```bash
mvn test
```

**Note 1:** You can build the tests without running them by using the following command:
```bash
mvn test-compile
```

**Note 2:** You can run a single test by using the following command:
```bash
mvn -Dtest=<test-class> test
# Example:
# mvn -Dtest=BNFieldsTest test
```
See [here](http://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html) for more information.

## Run syntax checker

Run:
```bash
mvn spotless:check
```

## Configuring AWS and using Flintrock to manage a testing cluster

### Create and configure an AWS account

1. Create an AWS account
2. Follow the set-up instructions [here](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/get-set-up-for-amazon-ec2.html)
    - Select the region
    - Create an EC2 keypair (see [here](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html#retrieving-the-public-key) for more info)
    - Create a security group

Both the security group and keypair are used to secure the EC2 instances launched, as indicated [here](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EC2_GetStarted.html). AWS takes care of creating a [default VPC](https://docs.aws.amazon.com/vpc/latest/userguide/default-vpc.html).

3. Create the appropriate set of IAM users
    - Create an `Administrator` as documented [here](https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html)
    - Create an IAM user for programmatic use with Flintrock. This user needs to have the following permissions:
        - `AmazonEC2FullAccess `,
        - `IAM.GetInstanceProfile` and `IAM.PassRole` (as documented [here](https://datawookie.dev/blog/2018/09/refining-an-aws-iam-policy-for-flintrock/))

### Using Flintrock

#### Installation

```console
python3.7 -m venv env
source env/bin/activate
pip install --upgrade pip
# Install the latest develop version of flintrock
pip install git+https://github.com/nchammas/flintrock

# Now the flintrock CLI is available
flintrock --help
```

*Note 1:* The latest stable version of Flintrock can be installed by simply running `pip install flintrock`. However, improvements have been added (and not yet packaged in a release) since the `1.0.0` release. In the following, we make the assumption that the [support for configurable JDKs](https://github.com/nchammas/flintrock/commit/6792626956412e61db7c266305a2a0cce7ece7dd) is available in the Flintrock CLI.

*Note 2:* Flintrock uses [boto3](https://boto3.amazonaws.com/v1/documentation/api/latest/index.html) which is the Python SDK for AWS.

*Note 3:* The `flintrock launch` command truly corresponds to clicking the `"Launch instance"` button on the EC2 dashboard. The values of the flags of the `flintrock launch` command correspond to the values that one needs to provide at the various steps of the "Launch instance" process (see [here](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/launching-instance.html#step-7-review-instance-launch))

#### Example

Below is an example to demonstrate how to launch a test cluster `test-cluster`.
Before doing so, we assume that:
- the private key (`.pem`) file of the created EC2 keypair (see [this step](#create-and-configure-an-aws-account)) is stored on your computer at: `~/.ssh/ec2-key.pem`
- the desired instance type is: `m4.large`
- the choosen AMI is one of the AMIs of either Amazon Linux 2 or the Amazon Linux AMI (see [here](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/finding-an-ami.html) to find an AMI). In fact, as documented [here](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/connection-prereqs.html) - the default username one can use to connect to the EC2 instance depends on the choosen AMI. For Amazon Linux (2) AMIs, this default username is `ec2-user`. For the sake of this example, we assume that the choosen AMI is: `ami-00b882ac5193044e4`
- the region is `us-east-1`

Furthermore, before instantiating a cluster with Flintrock, it is necessary to configure the environment with the credentials ("access key ID" and "secret access key") of the IAM programmatic user created in [previous steps](#create-and-configure-an-aws-account). This can either be done by configuring environment variables, or using a configuration file (as documented [here](https://boto3.amazonaws.com/v1/documentation/api/latest/guide/configuration.html#configuring-credentials).)

Once the environment is configured, and assuming the example values above, the command to launch the cluster becomes:
```console
flintrock launch test-cluster \
    --num-slaves 1 \
    --java-version 11 \
    --spark-version 3.0.0 \
    --ec2-instance-type m4.large \
    --ec2-region us-east-1 \
    --ec2-key-name ec2-key \
    --ec2-identity-file ~/.ssh/ec2-key.pem \
    --ec2-ami ami-00b882ac5193044e4 \
    --ec2-instance-initiated-shutdown-behavior terminate \
    --ec2-user ec2-user
```

-------------------

**TROUBLESHOOTING:** For debug purposes, it is possible to use the `aws` CLI directly. The CLI is available as a [docker container](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2-docker.html), however, while running the command like `docker run --rm -ti amazon/aws-cli <command>` is equivalent to running `aws <command>` on the host, one needs to remember that no state is preserved across the commands because the containers are removed as soon as the command stops executing. Hence, for a more stateful interaction, it is possible to override the `ENTRYPOINT` of the container by doing:
```console
docker run -it --entrypoint /bin/bash amazon/aws-cli
```

Then, in the container, the `aws` CLI can be used by running `aws <command>`. Note, that credentials need to be configured first via `aws configure`. To check the configured credentials, use `aws iam get-user"`
- If the access is denied, check:
    - The aws config (in `~/.aws` or your access key credentials in the container's environment)
    - The [time of your machine](https://stackoverflow.com/questions/24744205/ec2-api-error-validating-access-credential), and adjust to the same time of the AWS servers. On Debian-based distributions, this can be done via:
    ```console
    sudo apt-get --yes install ntpdate
    sudo ntpdate 0.amazon.pool.ntp.org
    ```

-------------------

Upon succesful deployment of the cluster, make sure to persist the Flintrock configuration in a configuration file. Then, the cluster can be inspected/stopped/started/destroyed/scaled etc by using the Flintrock commands (e.g. `flintrock describe test-cluster`, `flintrock destroy test-cluster` etc.)

## Benchmarks

We evaluate the distributed implementation of the zkSNARK setup and prover.
Below we use *instance size* to denote the number of constraints in an R1CS instance.

### libsnark *vs* DIZK

We measure the largest instance size (as a power of 2) that is supported by:

- the serial implementation of PGHR’s protocol in [libsnark](https://github.com/scipr-lab/libsnark)
- the serial implementation of Groth’s protocol in [libsnark](https://github.com/scipr-lab/libsnark)
- the distributed implementation of Groth's protocol in **DIZK**

<p align="center"><img src="https://user-images.githubusercontent.com/9260812/43099291-9203db9a-8e76-11e8-8d68-528d903500e1.png" width="68%"></p>

We see that using more executors allows us to support larger instance sizes,
in particular supporting billions of constraints with sufficiently many executors.
Instances of this size are much larger than what was previously possible via serial techniques.

### Distributed zkSNARK

We benchmark the running time of the setup and the prover on an increasing number of constraints and with an increasing number of executors.
Note that we do not need to evaluate the zkSNARK verifier as it is a simple and fast algorithm that can be run even on a smartphone.

<p align="center"><img src="https://user-images.githubusercontent.com/9260812/43099290-91ec40c0-8e76-11e8-8391-c30fbddc4acd.png" width="67%"></p>

<p align="center"><img src="https://user-images.githubusercontent.com/9260812/43099289-91d1d2b2-8e76-11e8-9a25-f06103903290.png" width="59%"></p>

Our benchmarks of the setup and the prover show us that:
 
1. For a given number of executors, running times increase nearly linearly as expected, demonstrating scalability over a wide range of instance sizes.

2. For a given instance size, running times decrease nearly linearly as expected, demonstrating parallelization over a wide range of number of executors.

## References

[Apa17] [_Apache Spark_](http://spark.apache.org/),
Apache Spark,
2017

[Bis06] [_Pattern recognition and machine learning_](https://www.springer.com/us/book/9780387310732),
Christopher M. Bishop,
*Book*, 2006

[BCCT12] [_From extractable collision resistance to succinct non-interactive arguments of knowledge, and back again_](http://eprint.iacr.org/2011/443),
Nir Bitansky, Ran Canetti, Alessandro Chiesa, Eran Tromer,
*Innovations in Theoretical Computer Science* (ITCS), 2012

[BCIOP13] [_Succinct non-interactive arguments via linear interactive proofs_](http://eprint.iacr.org/2012/718),
Nir Bitansky, Alessandro Chiesa, Yuval Ishai, Rafail Ostrovsky, Omer Paneth,
*Theory of Cryptography Conference* (TCC), 2013

[BGMW93] [_Fast exponentiation with precomputation_](https://link.springer.com/chapter/10.1007/3-540-47555-9_18),
Ernest F. Brickell, Daniel M. Gordon, Kevin S. McCurley, and David B. Wilson,
*International Conference on the Theory and Applications of Cryptographic Techniques* (EUROCRYPT), 1992

[BT04] [_Barycentric Lagrange interpolation_](https://people.maths.ox.ac.uk/trefethen/barycentric.pdf),
Jean-Paul Berrut and Lloyd N. Trefethen,
*SIAM Review*, 2004

[Can69] [_A cellular computer to implement the Kalman filter algorithm_](https://dl.acm.org/citation.cfm?id=905686),
Lynn E Cannon,
*Doctoral Dissertation*, 1969

[Gro16] [_On the size of pairing-based non-interactive arguments_](https://eprint.iacr.org/2016/260.pdf),
Jens Groth,
*International Conference on the Theory and Applications of Cryptographic Techniques (EUROCRYPT)*, 2016

[LRF97] [_Generalized cannon’s algorithm for parallel matrix multiplication_](https://dl.acm.org/citation.cfm?id=263591),
Hyuk-Jae Lee, James P. Robertson, and Jose A. B. Fortes,
*International Conference on Supercomputing*, 1997

[NT16] [_Photoproof: Cryptographic image authentication for any set of permissible transformations_](https://www.cs.tau.ac.il/~tromer/papers/photoproof-oakland16.pdf),
Assa Naveh and Eran Tromer,
*IEEE Symposium on Security and Privacy*, 2016

[Pip76] [_On the evaluation of powers and related problems_](https://ieeexplore.ieee.org/document/4567910/),
Nicholas Pippenger,
*Symposium on Foundations of Computer Science* (FOCS), 1976

[Pip80] [_On the evaluation of powers and monomials_](https://pdfs.semanticscholar.org/7d65/53e185fd90a855717ee915992e17f38c99ae.pdf),
Nicholas Pippenger,
*SIAM Journal on Computing*, 1980

[Sze11] [_Schönhage-Strassen algorithm with MapReduce for multiplying terabit integers_](https://people.apache.org/~szetszwo/ssmr20110429.pdf),
Tsz-Wo Sze,
*International Workshop on Symbolic-Numeric Computation*, 2011

[vW97] [_SUMMA: scalable universal matrix multiplication algorithm_](https://dl.acm.org/citation.cfm?id=899248),
Robert A. van de Geijn and Jerrell Watts,
*Technical Report*, 1997

[Wil16] [_Strong ETH breaks with Merlin and Arthur: short non-interactive proofs of batch evaluation_](https://arxiv.org/pdf/1601.04743.pdf),
Ryan Williams,
*Conference on Computational Complexity*, 2016

## Acknowledgements

This work was supported by Intel/NSF CPS-Security grants,
the [UC Berkeley Center for Long-Term Cybersecurity](https://cltc.berkeley.edu/),
and gifts to the [RISELab](https://rise.cs.berkeley.edu/) from Amazon, Ant Financial, CapitalOne, Ericsson, GE, Google, Huawei, IBM, Intel, Microsoft, and VMware.
The authors thank Amazon for donating compute credits to RISELab, which were extensively used in this project.

## License

[MIT License](LICENSE)
