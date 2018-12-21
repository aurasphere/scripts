# Microsoft Office 365
This folder contains configuration files for Office365 installation. The configurations will install Office365, Visio 2019 and Project 2019 and automatically activate them.

To use these configurations, you will have to download the Office365 setup executable from the Microsoft Partnerzone and run it with the command:

    setup.exe /configure PATH_TO_CONFIGURATION_FILE
	
Before running them, remember to replace the INSERT_KEY_HERE string with actual valid activation code or to remove the AUTOACTIVATE property along with the PIDKEY attributes on the products to use manual activation instead.