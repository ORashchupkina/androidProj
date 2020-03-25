const Pool = require('pg-pool')
const pool = new Pool({
    user: 'postgres',
    host: 'localhost',
    database: 'mobia_app',
    password: 'kl0pik',
    port: 5432,
    ssl: false,
    max: 8, // set pool max size to 20
    min: 4, // set min pool size to 4
    idleTimeoutMillis: 100000, // close idle clients after 1 second
    connectionTimeoutMillis: 100000
});


const assert = require('assert');
const daotkeys = require('../routes/database_dao/tscriptstomob_dao.js');
const UUID = require('uuid/v4')

describe('test group', () => {
    it('should do something 1: insScriptToMob', () => {
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'INSERT INTO T_USER VALUES(\'login_1\', \'pass_1\')', 'INSERT', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'SELECT * FROM T_USER', 'SELECT', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'UPDATE T_USER SET PASS_HASH = \'pass_2\' WHERE LOGIN = \'login_1\'', 'UPDATE', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'SELECT * FROM T_USER', 'SELECT', "local.db", null, null);
        var res = daotkeys.insScriptToMob(pool, null,"user", 'DELETE FROM T_USER WHERE LOGIN = \'login_1\'', 'DELETE', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'SELECT * FROM T_USER', 'SELECT', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'INSERT INTO TJOBS (ID, DTC, ID_EXECUTOR, ID_JOBS_ADDR_EXT, ADDR_NAME, ID_JOBS_STATE_EXT, TO_F_R, TO_F_W, TO_F_C, TO_M_W, ID_JOBS_PROPERTIES_EXT, CLIENT_NAME, CLIENT_PHONE, ZAYAVITEL, KOD_ZAYAV, NUM_ZAYAV, UVTU, ID_UVTU, DT_VISIT_PLAN) VALUES(\'{0E9EC670-0412-44A6-A7C0-FC8AA1DC343A}\', \'2020-12-10 10:45:20\', 1000, \'{74D99DD1-FD9D-4FC8-8CEC-61B200B91BB4}\', \'Восстания 24\', \'{D44F854B-7B5B-4906-A9A7-7B1A8FFD3D1E}\', \'2020-12-10 10:45:20\', \'2020-12-10 10:45:20\', \'2020-12-10 10:45:20\', \'2020-12-10 10:45:20\', \'{C83D9D41-1B16-4C02-948A-D428C36CE390}\',\'Иван И.И.\', \'893214567852\',\'ИвановКорпорейшен\',\'123456\',\'0123456\',\'456123\',\'0456123\', \'2020-02-20 10:45:20\'); COMMIT', 'INSERT', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'SELECT * FROM TJOBS', 'SELECT', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'UPDATE TJOBS SET ID_EXECUTOR = \'2111\' WHERE ID = \'{0E9EC670-0412-44A6-A7C0-FC8AA1DC343A}\'', 'UPDATE', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'SELECT * FROM TJOBS', 'SELECT', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'DELETE FROM TJOBS WHERE ID = \'{0E9EC670-0412-44A6-A7C0-FC8AA1DC343A}\'', 'DELETE', "local.db", null, null);
        // var res = daotkeys.insScriptToMob(pool, null,"user", 'SELECT * FROM TJOBS', 'SELECT', "local.db", null, null);
        // assert.fail();

    })

    it('should do something 2: getScriptToMobNew', () => {
        var res = daotkeys.getScriptToMobNew(pool, "user_1").then( function (response ) {
            let s='';
            // response.rows[0].uuid
        }).catch(function (err) {
            sendError(rsp, "{F13A194E-84B8-4E2C-B77F-6C55C0D18991}");
        });

    })

    it('should do something 2: updDtSendByKey', () => {
        var res = daotkeys.updDtSendByKey(pool, new Date().toLocaleString(), "a7104fa5-a741-4ffd-8d6e-a463d8a4938d").then( function (response ) {
            let s='';
            // response.rows[0].uuid
        }).catch(function (err) {
            sendError(rsp, "{B22044B1-B943-48C2-B3FE-3339BBDDAAAF}");
        });

    })
});
