const functions = require("firebase-functions");

const admin = require("firebase-admin");
admin.initializeApp();

// users collection
//      |- user
//      |- user
//        |- uid
//        |- token
// update token -> set in "users", then query "lists" and update
// create list -> set in "lists" the notification key with the creators token
// add users to list -> very simple

exports.updateToken = functions.region("europe-west1")
    .https.onCall(async (data, context) => {
      const json = JSON.parse(data);
      return admin.firestore().collection("users").doc(json.uid)
          .set({token: json.token})
          .then((response) => {
            console.log("updated token of user " + json.uid);
            return {success: true};
          })
          .catch((error) => {
            console.log("failed to update token of user " + json.uid);
            return {success: false};
          });
    });

exports.sendMessage = functions.region("europe-west1")
    .https.onCall(async (data, context) => {
      const json = JSON.parse(data);

      const list = await admin.firestore().collection("lists")
          .doc(json.listId).get();

      const users = list.data().users;
      const index = users.indexOf(json.uid);
      if (index > -1) users.splice(index, 1);

      const tokens = users
          .map(async (uid) => {
            const userDoc = await admin.firestore().collection("users")
                .doc(uid).get();
            return userDoc.data().token;
          });

      return Promise.all(tokens).then((tokens) => {
        const message = {
          data: {data: data},
          tokens: tokens.filter((t) => typeof t === "string"),
        };
        admin.messaging().sendMulticast(message)
            .then((response) => {
              console.log(response.successCount +
                  " messages were sent successfully");
              if (response.failureCount > 0) {
                const failedTokens = [];
                response.responses.forEach((resp, i) => {
                  if (!resp.success) failedTokens.push(message.tokens[i]);
                });
                console.log("list of tokens that caused failures: " +
                    failedTokens);
              }
              return {success: true};
            })
            .catch((error) => {
              console.log("error sending message", error);
              return {success: false};
            });
      });
    });
