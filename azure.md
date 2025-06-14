# Azure Credentials Setup Guide

## Step 1: Install Azure CLI

First, install the Azure CLI on your local machine:

### Windows
Download from: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli-windows

### macOS
```bash
brew install azure-cli
```

### Linux (Ubuntu/Debian)
```bash
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
```

## Step 2: Login to Azure

```bash
az login
```

This will open your browser for authentication. Sign in with your Azure account.

## Step 3: Get Your Subscription ID

```bash
az account list --output table
```

Copy your subscription ID from the output.

## Step 4: Create Service Principal

Replace `{subscription-id}` with your actual subscription ID:

```bash
az ad sp create-for-rbac \
  --name "github-actions-arebbus" \
  --role contributor \
  --scopes /subscriptions/{subscription-id} \
  --sdk-auth
```

**Example:**
```bash
az ad sp create-for-rbac \
  --name "github-actions-arebbus" \
  --role contributor \
  --scopes /subscriptions/12345678-1234-1234-1234-123456789012 \
  --sdk-auth
```

## Step 5: Copy the JSON Output

The command will output JSON like this:

```json
{
  "clientId": "12345678-1234-1234-1234-123456789012",
  "clientSecret": "your-client-secret-here",
  "subscriptionId": "12345678-1234-1234-1234-123456789012",
  "tenantId": "12345678-1234-1234-1234-123456789012",
  "activeDirectoryEndpointUrl": "https://login.microsoftonline.com",
  "resourceManagerEndpointUrl": "https://management.azure.com/",
  "activeDirectoryGraphResourceId": "https://graph.windows.net/",
  "sqlManagementEndpointUrl": "https://management.core.windows.net:8443/",
  "galleryEndpointUrl": "https://gallery.azure.com/",
  "managementEndpointUrl": "https://management.core.windows.net/"
}
```

**⚠️ Important:** Copy this entire JSON output - you'll need it for GitHub secrets.

## Step 6: Create Azure Container Registry

```bash
# Create resource group (if you don't have one)
az group create --name "rg-arebbus" --location "East US"

# Create container registry
az acr create \
  --resource-group "rg-arebbus" \
  --name "acrarebbus" \
  --sku Standard \
  --admin-enabled
```

## Step 7: Get ACR Credentials

```bash
# Get ACR login server
az acr show --name "acrarebbus" --query loginServer --output tsv

# Get ACR admin credentials
az acr credential show --name "acrarebbus"
```

This will output:
```json
{
  "passwords": [
    {
      "name": "password",
      "value": "your-acr-password-here"
    },
    {
      "name": "password2", 
      "value": "your-second-acr-password-here"
    }
  ],
  "username": "acrarebbus"
}
```

## Step 8: GitHub Secrets Setup

Go to your GitHub repository → Settings → Secrets and variables → Actions

Add these secrets:

| Secret Name | Value | Example |
|-------------|-------|---------|
| `AZURE_CREDENTIALS` | The entire JSON from Step 5 | `{"clientId": "12345..."}` |
| `ACR_NAME` | Your ACR name (without .azurecr.io) | `acrarebbus` |
| `ACR_USERNAME` | Username from Step 7 | `acrarebbus` |
| `ACR_PASSWORD` | Password from Step 7 | `your-acr-password-here` |
| `AZURE_RESOURCE_GROUP` | Your resource group name | `rg-arebbus` |
| `ACI_NAME` | Desired container instance name | `aci-arebbus-backend` |

## Step 9: Verify Setup

Test your service principal:

```bash
# Login with service principal
az login --service-principal \
  --username "your-client-id" \
  --password "your-client-secret" \
  --tenant "your-tenant-id"

# Test ACR access
az acr login --name "acrarebbus"
```

## Troubleshooting

### If ACR login fails:
```bash
# Enable admin user on ACR
az acr update --name "acrarebbus" --admin-enabled true
```

### If permissions are insufficient:
```bash
# Assign additional roles if needed
az role assignment create \
  --assignee "your-client-id" \
  --role "AcrPush" \
  --scope "/subscriptions/{subscription-id}/resourceGroups/rg-arebbus/providers/Microsoft.ContainerRegistry/registries/acrarebbus"
```

## Security Best Practices

1. **Use unique names** - ACR names must be globally unique
2. **Limit scope** - The service principal only has access to your subscription
3. **Rotate secrets** - Periodically regenerate the service principal credentials
4. **Monitor usage** - Check Azure Activity Log for service principal actions

## Next Steps

Once you've set up all the secrets, push to your `main` branch and the GitHub Actions workflow will automatically:
1. Build your application
2. Push to Azure Container Registry
3. Deploy to Azure Container Instance
4. Provide the public URL