# This script removes the `IMAGE_STORAGE_PATH` environment variable for the current user.
# To run this script, open PowerShell, navigate to the project root,
# and execute: .\remove-dev-env.ps1

$VarName = "IMAGE_STORAGE_PATH"

Write-Host "Removing environment variable '$VarName' for the current user..."

try {
    # To remove a persistent environment variable, set its value to $null.
    [System.Environment]::SetEnvironmentVariable($VarName, $null, "User")
    Write-Host ""
    Write-Host "SUCCESS: Environment variable '$VarName' has been removed."
    Write-Host "Please restart your terminal or IDE (like IntelliJ) for the changes to take effect."
}
catch {
    Write-Host ""
    Write-Host "ERROR: Failed to remove the environment variable. Please make sure you have the necessary permissions."
    Write-Host "Error details: $($_.Exception.Message)"
}