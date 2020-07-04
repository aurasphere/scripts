ECHO Downloading and installing Chocolatey
@powershell -NoProfile -ExecutionPolicy Bypass -Command "iex ((new-object net.webclient).DownloadString('https://chocolatey.org/install.ps1'))" && SET PATH=%PATH%;%ALLUSERSPROFILE%\chocolatey\bin

ECHO Installing apps

ECHO Configure chocolatey
choco feature enable -n allowGlobalConfirmation

ECHO Installing browsers
choco install googlechrome
choco install firefox

ECHO Installing utilities
choco install megasync
choco install winrar
choco install adobereader
choco install teamviewer
choco install ccleaner
choco install k-litecodecpackfull
choco install razer-synapse-2

ECHO Configuring Java development environment
choco install jdk8
choco install jdk11
choco install eclipse-java-oxygen
choco install javadecompiler-gui
choco install maven

ECHO Installing other development tools
choco install notepadplusplus
choco install git
choco install tortoisegit
choco install postman
choco install filezilla
choco install mobaxterm	
choco install cygwin
choco install virtualbox
choco install winmerge
choco install heroku-cli

ECHO Configuring mobile/web development environment
choco install nodejs
choco install yarn
choco install vscode
choco install vscode-react-native
choco install androidstudio
choco install android-sdk

ECHO Installing other programming languages
choco install python3
choco install python2
choco install ruby
choco install arduino

ECHO Installing databases
choco install mysql
choco install mysql.workbench
choco install mongodb

ECHO Installing communication software
choco install skype
choco install discord
choco install grammarly

ECHO The following software needs to be installed manually:
ECHO - Desktop Ticker
ECHO - Wondershare Filmora
ECHO - Repetier Host

choco feature disable -n allowGlobalConfirmation