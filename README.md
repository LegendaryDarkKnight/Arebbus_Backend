# Required GitHub Secrets

Add these secrets to your GitHub repository settings (Settings → Secrets and variables → Actions):

## Docker Hub Secrets
```
DOCKER_USERNAME         # Your Docker Hub username
DOCKER_PASSWORD         # Your Docker Hub password/token
```

## Azure Secrets
```
AZURE_CREDENTIALS       # Azure service principal JSON
AZURE_RESOURCE_GROUP    # Your Azure resource group name
ACR_NAME               # Your Azure Container Registry name (without .azurecr.io)
ACR_USERNAME           # ACR username
ACR_PASSWORD           # ACR password
ACI_NAME               # Your Azure Container Instance name
```

## Application Secrets
```
POSTGRES_URL           # Your PostgreSQL connection URL
POSTGRES_USER          # PostgreSQL username
POSTGRES_PASS          # PostgreSQL password
SECRET_KEY             # Your application secret key
FRONTEND_URL           # Your frontend application URL
```

## Azure Credentials Format
The `AZURE_CREDENTIALS` should be a JSON object like this:
```json
{
  "clientId": "your-client-id",
  "clientSecret": "your-client-secret",
  "subscriptionId": "your-subscription-id",
  "tenantId": "your-tenant-id"
}
```

You can create this using Azure CLI:
```bash
az ad sp create-for-rbac --name "github-actions" --role contributor \
    --scopes /subscriptions/{subscription-id} --sdk-auth
```