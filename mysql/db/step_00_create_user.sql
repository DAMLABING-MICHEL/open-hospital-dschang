#login as root

# Substitute '%' or whatever with host name or IP address.

# MySQL does not provide a "DROP USER IF EXIST" statement so
# this is a workaround to create the user and grant 
# him permissions on the 'oh_dschang' database.
GRANT ALL ON oh_dschang.* TO 'isf'@'localhost' IDENTIFIED BY 'isf123';
GRANT ALL ON oh_dschang.* TO 'isf'@'%' IDENTIFIED BY 'isf123';
