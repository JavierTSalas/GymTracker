import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';


var serviceAccount = require('../gymtracker-d17d9-ccd6fafd915f.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: 'https://gymtracker-d17d9.firebaseio.com'
});




// Get the Firestore client for the default app
var db = admin.firestore();

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript
exports.extractFromAuthToTable = functions.auth.user().onCreate((user) => {
    
    return admin.auth().getUser(user.uid)
        .then(function (userRecord) {
            console.log("Successfully fetched user data:", userRecord.toJSON());
            return writeUserData(user.uid);
        })
        .catch(function (error) {
            console.log("Error fetching user data:", error);
        });


});

function writeUserData(userId : any) {

    var data = {
        '_ID': userId,
		'gymID': 'null',
    };


    // Create default document for FCM_tokens 
    var fcm_data = {
        device_ids: [""],
        client_tokens: [""]
    };

    // Get a new write batch
    var batch = db.batch();


    // Default FCM data
    var FCM_Ref = db.collection('FCM_tokens').doc(userId);
    batch.set(FCM_Ref, fcm_data);

	
    // Update the document relavent to our user
    // Merge since we might have a race condition on the display being written in app and this function executing
    var User_Ref = db.collection('users').doc(userId);
    batch.set(User_Ref, data, { merge: true });


    // Commit the batch
    return batch.commit().then(function () {
        console.log("Batch written successful!");
        // ...
    });

}
