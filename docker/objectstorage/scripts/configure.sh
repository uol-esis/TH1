#!/bin/sh

alias garage="docker exec garaged /garage"

echo "========================="
echo "STARTING CONFIGURATION";
echo "========================="

if [ "$(docker exec garaged /garage bucket list | grep -c th1-bucket)" -eq 1 ]; then
  echo "BUCKET ALREADY PRESENT. NOTHING TO DO."
else
  echo "CONFIGURING NODE"
  NODE_ID=$(garage status | awk '/^==== HEALTHY NODES ====/ {found=1; next;next} found && NF {if (++line == 2) {print $1; exit}}')

  garage layout assign -z dc1 -c 1G "$NODE_ID"
  garage layout apply --version 1
  echo "NODE CONFIGURATION DONE"

  echo "========================="

  echo "CREATING BUCKET"
  garage bucket create th1-bucket
  garage bucket list
  garage bucket info th1-bucket


  echo "CREATING KEY"
  # creating the key to access it
  output=$(garage key create th1-app-key)
  KEY_ID=$(echo "$output" | awk -F': *' '/Key ID:/ {print $2}')
  SECRET_KEY=$(echo "$output" | awk -F': *' '/Secret key:/ {print $2}')
  garage key list
  garage key info th1-app-key

  echo "GRANTING KEY ACCESS TO BUCKET"
  garage bucket allow \
    --read \
    --write \
    --owner \
    th1-bucket \
    --key th1-app-key

  echo "FINAL SUMMARY"
  garage bucket info th1-bucket
  echo "================"
  echo "================"
  echo "THE FOLLOWING KEY HAS BEEN GENERATED"
  echo "================"
  echo "KEY ID: $KEY_ID"
  echo "SECRET_KEY: $SECRET_KEY"
fi