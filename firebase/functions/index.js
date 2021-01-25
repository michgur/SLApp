const functions = require("firebase-functions");

const admin = require('firebase-admin');
admin.initializeApp();

// users collection
//          |- user
//          |- user
//              |- uid
//              |- token
// update token -> set in 'users', then query 'lists' and update
// create list -> set in 'lists' the notification key with the creators token
// add users to list -> very simple

exports.updateToken = functions.https.onRequest(async (req, res) => {
    const uid = req.query.uid;
    const token = req.query.token;

    admin.firestore().collection('users').doc(uid).set({ token: token })
        .then((response) => {
            console.log('updated token of user ' + uid)
            res.json({ success: true });
         })
        .catch((error) => {
            console.log('failed to update token of user ' + uid)
            res.json({ success: false });
        });
});

exports.sendMessage = functions.https.onRequest(async (req, res) => {
    const listId = req.query.listId;
    const data = req.query.data;

    const list = await admin.firestore().collection('lists').doc(listId).get();
    const users = list.data().users;
    const tokens = users.map(async (uid) => {
        const userDoc = await admin.firestore().collection('users').doc(uid).get();
        return userDoc.data().token;
    });

    const message = {
        data: data,
        tokens: tokens
    };
    admin.messaging().sendMulticast(message)
        .then((response) => {
            console.log(response.successCount + ' messages were sent successfully');
            if (response.failureCount > 0) {
                const failedTokens = [];
                response.responses.forEach((resp, i) => {
                    if (!resp.success) failedTokens.push(tokens[i]);
                });
                console.log('list of tokens that caused failures: ' + failedTokens);
            }
            res.json({ success: true });
        })
        .catch((error) => {
            console.log('error sending message', error);
            res.json({ success: false });
        });
});