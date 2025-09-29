# This script sets up the environment variable required for local development on Windows.
# It creates a persistent environment variable `IMAGE_STORAGE_PATH` for the current user.
# To run this script, open PowerShell as an administrator, navigate to the project root,
# and execute: .\set-dev-env.ps1

$VarName = "IMAGE_STORAGE_PATH"
$VarValue = "C:\pet-manage"

Write-Host "Setting environment variable '$VarName' to '$VarValue' for the current user..."

try {
    # Set a persistent environment variable for the current user.
    [System.Environment]::SetEnvironmentVariable($VarName, $VarValue, "User")
    Write-Host ""
    Write-Host "SUCCESS: Environment variable '$VarName' has been set."
    Write-Host "Please restart your terminal or IDE (like IntelliJ) for the changes to take effect."
}
catch {
    Write-Host ""
    Write-Host "ERROR: Failed to set the environment variable. Please make sure you have the necessary permissions."
    Write-Host "You might need to run this script as an administrator."
    Write-Host "Error details: $($_.Exception.Message)"
}