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

exports.updateEquipmentLogs = functions.firestore.document('/gyms/{gym}/equipment/{equipmentID}')
	.onUpdate((change, context) => {
		if(change && change.after && change.after.data && change.before && change.before.data){
		  const date = new Date();
		  const equipmentID = context.params.equipmentID;
		  const data = change.after.data();
		  const prevData = change.before.data();
		  if(data && prevData){
			  if(data.used != true && prevData.used != false){
				  return null;
			  }
			  
			  console.log('Equipment Usage Detected', context.params.gym, equipmentID);
			  var log_ref = db.collection('gyms').doc(context.params.gym).collection('logs').doc(getFormattedDate(date));
			  var getDoc = log_ref.get()
				.then(doc => {
					if(!doc.exists) {
						console.log('no such document 999');
					} else {
						console.log('doc data: ', doc.data());
					}
					var logData = doc.data();
					if(logData){
						if(logData[equipmentID] == undefined){
							logData[equipmentID] = 1;
						} else {
							logData[equipmentID] = logData[equipmentID]+1;
						}
						var set_doc = log_ref.set(logData, {merge: true});
						console.log(set_doc);
					}
					else{
						type logDict = { [key: string]: number };
						var newData: logDict = {};
						newData[equipmentID] = 1;
						var set_doc1 = log_ref.set(newData);
						console.log(set_doc1);
					}
				})
				.catch(err => {
					console.log('Error getting document', err);
				});
				console.log(getDoc);
			  
			  
			  return change.after.ref;
		  }
		  else { return null; }
		}
		else { return null }
    });

function getFormattedDate(date: Date){
	var year = date.getFullYear();
	
	var month = (1+date.getMonth()).toString();
	month = month.length > 1 ? month : '0' + month;

	var day = date.getDate().toString();
	day = day.length > 1 ? day : '0' + day;
  
	return month + day + year;
}

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
