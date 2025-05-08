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
