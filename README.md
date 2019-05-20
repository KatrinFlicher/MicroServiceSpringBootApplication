---Contacts---
Author: Zaretskaya Katsiaryna Valerievna
email: puperlastik@mail.ru // katrinflicher@gmail.com

---

## Conditions
Entities:
1) Collections (name, cache limit, algorithm, json scheme)
2) Documents (key (String), value (JSON))

Actions:
* Collections: create, delete, get (all fields), update (name, cache limit, algorithm), list
* Objects: CRUD + list

## Description
* There are 2 configuration files with nodes and properties of databases.
* At the start of application we set the name of node. Example: -Dname=node1
Properties of Database, port of application depend on this name. See AppConfig
Also you can set paths to files with configuration of nodes and database using command line.
* When we send request, for collection write operations execute in current node and then send request to all others, 
for documents first of all there're document group definition and after that either redirect request to necessary group
or execution on the current node and send request to replicas.    

## Features:
* Json schema - store on the collection creation and then validate every single write request (except delete)
* all list endpoints must have pagination
* use database as primary storage
* all endpoints must be designed as perfect REST