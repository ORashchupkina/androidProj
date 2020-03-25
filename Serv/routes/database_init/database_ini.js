const connectionStringBase = 'postgres://postgres:kl0pik@localhost:5432/';
const connectionStringRoot = connectionStringBase + 'postgres';
const db_name = 'mobia_app';
const def_schema ='public';
const connectionString = connectionStringBase + db_name;

module.exports = {def_schema: def_schema, connectionStringBase: connectionStringBase, connectionStringRoot: connectionStringRoot, db_name:db_name, connectionString:connectionString}