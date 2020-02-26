#!/bin/bash
echo "generating .env from Travis environment variables for CI tests"
cat << EOF > .env
# generating vars for tests from CI Env variables
# Application ID
virgil_appId=${virgil_appId}
# Application token. AT.****
virgil_at=${virgil_at}
# Nonrotatable master secret. NM.****
virgil_nms=${virgil_nms}
# Backup public key. BU.****
virgil_bu=${virgil_bu}
# App secret key. SK.****
virgil_sk=${virgil_sk}
# Service public key. PK.****
virgil_pk=${virgil_pk}
virgil_pheServiceAddress=${virgil_pheServiceAddress}
virgil_kmsServiceAddress=${virgil_kmsServiceAddress}
EOF

ls -la ./
