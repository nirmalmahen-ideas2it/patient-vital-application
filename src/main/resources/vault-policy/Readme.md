# Vault AppRole Authentication and Policy Setup Guide

This guide outlines the steps for enabling AppRole-based authentication and configuring policies in Vault.

## Step 1: Enable AppRole Authentication Method

1. Enable the AppRole authentication method in Vault:

    ```bash
    vault auth enable approle
    ```

   **Output:**

    ```
    Success! Enabled approle auth method at: approle/
    ```

## Step 2: Create AppRole Role

1. Create a role with the required parameters such as `secret_id_ttl`, `token_ttl`, `token_max_ttl`, and `policies`. In
   this case, we're attaching the `patientvitalapplication-dev-policy` to the role:

    ```bash
    vault write auth/approle/role/patientvitalapplication-dev-role \
         secret_id_ttl=24h \
         token_num_uses=100 \
         token_ttl=20h \
         token_max_ttl=24h \
         policies=default,patientvitalapplication-dev-policy
    ```

   **Output:**

    ```
    Success! Data written to: auth/approle/role/patientvitalapplication-dev-role
    ```

## Step 3: Retrieve Role ID

1. Fetch the `role_id` for the newly created role:

    ```bash
    vault read auth/approle/role/patientvitalapplication-dev-role/role-id
    ```

   **Output (with sensitive values hidden):**

    ```
    Key        Value
    ---        -----
    role_id    <role_id>
    ```

## Step 4: Generate Secret ID

1. Generate the `secret_id` for the role:

    ```bash
    vault write -f auth/approle/role/patientvitalapplication-dev-role/secret-id
    ```

   **Output (with sensitive values hidden):**

    ```
    Key                   Value
    ---                   -----
    secret_id             <secret_id>
    secret_id_accessor    <secret_id_accessor>
    secret_id_num_uses    0
    secret_id_ttl         24h
    ```

## Step 5: Create/Update Policies

1. Create the policy file `patientvitalapplication-policy.hcl` with the required capabilities. Below is an example
   policy for
   reading and
   managing the secrets in `secret/patientvitalapplication/dev`:

    ```hcl
    path "secret/patientvitalapplication/dev" {
      capabilities = ["create", "read", "update", "delete", "list"]
    }

    path "secret/data/patientvitalapplication/dev" {
      capabilities = ["read"]
    }

    path "secret/data/patientvitalapplication" {
      capabilities = ["read"]
    }

    path "secret/metadata/patientvitalapplication/dev" {
      capabilities = ["read", "list"]
    }
    ```

2. Write the policy to Vault:

    ```bash
    vault policy write patientvitalapplication-dev-policy /path/to/patientvitalapplication-dev-policy.hcl
    ```

   **Note:** If you're running Vault in a container, copy the `patientvitalapplication-policy.hcl` file to the container
   first:

    ```bash
    docker cp ./patientvitalapplication-dev-policy.hcl <container_id>:/patientvitalapplication-dev-policy.hcl
    ```

   Then, execute the following inside the container:

    ```bash
    vault policy write patientvitalapplication-dev-policy /patientvitalapplication-dev-policy.hcl
    ```

## Step 6: Verify the Policy

1. Verify that the policy has been written correctly:

    ```bash
    vault policy read patientvitalapplication-dev-policy
    ```

   **Output (with sensitive values hidden):**

    ```
    # patientvitalapplication-policy.hcl
    path "secret/patientvitalapplication/dev" {
      capabilities = ["create", "read", "update", "delete", "list"]
    }

    path "secret/data/patientvitalapplication/dev" {
      capabilities = ["read"]
    }

    path "secret/data/patientvitalapplication" {
      capabilities = ["read"]
    }

    path "secret/metadata/patientvitalapplication/dev" {
      capabilities = ["read", "list"]
    }
    ```

## Step 7: Attach the Policy to the Role

1. After updating the policy, you need to attach it again to the role:

    ```bash
    vault write auth/approle/role/patientvitalapplication-dev-role token_policies="patientvitalapplication-dev-policy"
    ```

   **Output:**

    ```
    Success! Data written to: auth/approle/role/patientvitalapplication-dev-role
    ```

## Step 8: Generate Secret ID and Use it

1. To generate a new `secret_id`, run the following command:

    ```bash
    vault write -f auth/approle/role/patientvitalapplication-dev-role/secret-id
    ```

   **Output (with sensitive values hidden):**

    ```
    Key                   Value
    ---                   -----
    secret_id             <secret_id>
    secret_id_accessor    <secret_id_accessor>
    secret_id_num_uses    0
    secret_id_ttl         24h
    ```

## Step 9: Authenticate Using AppRole

1. Use the `role_id` and `secret_id` to authenticate and receive a token:

    ```bash
    vault write auth/approle/login role_id=<role_id> secret_id=<secret_id>
    ```

   **Output (with sensitive values hidden):**

    ```
    Key              Value
    ---              -----
    auth.client_token <client_token>
    ```

   Use this client token for making further requests to Vault.

## Conclusion

You have successfully enabled AppRole-based authentication in Vault, created a policy, and assigned it to the AppRole.
Now you can authenticate using the `role_id` and `secret_id`, and perform actions based on the assigned policies.
