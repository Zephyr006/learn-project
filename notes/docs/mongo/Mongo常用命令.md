
切换数据库：`use db_name;`

创建用户：`db.createUser({ user: "user_name", pwd: "password",
            roles: [
                { role: "readWrite", db: "database_name" }
                ,{ role: "dbAdmin", db: "database_name" }   
            ] });`
            
创建Collection：`db.createCollection("collection_name");`
            
查询指定collection数据总数：`db.collection_name.count();`

