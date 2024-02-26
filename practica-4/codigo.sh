virtualhost.conf
<VirtualHost *:80>
    ServerAdmin admin@admin.com
    DocumentRoot "/var/www/html/app1"
    ServerName app1.digitalcv.me
    RewriteEngine on
    RewriteCond %{SERVER_NAME} -app1.digitalcv.me
    RewriteRule ^ https://%{SERVER_NAME}%{REQUEST_URI} [END,NE,R=permanent]
</VirtualHost>

<VirtualHost *:80>
    ServerAdmin admin@admin.com
    DocumentRoot "/var/www/html/app2"
    ServerName app2.digitalcv.me
    RewriteEngine on
    RewriteCond %{SERVER_NAME} -app2.digitalcv.me
    RewriteRule ^ https://%{SERVER_NAME}%{REQUEST_URI} [END,NE,R=permanent]
</VirtualHost>

 
proxyreverso.conf
<VirtualHost *:80>
ServerAdmin admin@admin.com
ServerName digitalcv.me

ProxyPreserveHost On
ProxyRequests On

    <Proxy *>
         Order deny,allow
         Allow from all
    </Proxy>

     ProxyPass               / http://localhost:7000/
     ProxyPassReverse        / http://localhost:7000/

    ErrorLog /var/log/error.log
    TransferLog /var/log/access.log

</VirtualHost>

 
seguro.conf
<VirtualHost *:80>
ServerAdmin admin@admin.com
ServerName digitalcv.me

Redirect 301 / https://digitalcv.me/

</VirtualHost>
<VirtualHost *:443>

        servername digitalcv.me
        ServerAdmin admin@admin.com

        SSLEngine on
        SSLCertificateFile /etc/letsencrypt/live/digitalcv.me/cert.pem
        SSLCertificateKeyFile /etc/letsencrypt/live/digitalcv.me/privkey.pem
        SSLCertificateChainFile /etc/letsencrypt/live/digitalcv.me/chain.pem

ProxyPreserveHost On
ProxyRequests On

    <Proxy *>
         Order deny,allow
         Allow from all
    </Proxy>

     ProxyPass               / http://localhost:7000/
     ProxyPassReverse        / http://localhost:7000/

    ErrorLog /var/log/error.log
    TransferLog /var/log/access.log

</VirtualHost>
