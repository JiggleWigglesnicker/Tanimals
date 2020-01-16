const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

exports.modifyUser = functions.firestore
    .document('match/{userId}')
    .onWrite((change, context) => {

      Object.filter = function( obj, predicate) {
        var result = {}, key;
        // ---------------^---- as noted by @CMS, 
        //      always declare variables with the "var" keyword
    
        for (key in obj) {
            if (obj.hasOwnProperty(key) && !predicate(obj[key])) {
                result[key] = obj[key];
            }
        }
    
        return result;
    };

      const userId = context.params.userId;

      // Get an object with the current document value.
      // If the document does not exist, it has been deleted.
      const document = change.after.exists ? change.after.data() : null;

      // Get an object with the previous document value (for update or delete)
      const oldDocument = change.before.data();

      let differenceArr = Object.keys(document).filter(x => !Object.keys(oldDocument).includes(x));
      let difference = differenceArr[0] //userid - document[difference] == boolean

      if(document[difference] === true){
        var names = [userId, difference]
        names.sort()
        var channelName = names[0]+names[1]

        let data = {
          userId: true,
        };
        
        // Add a new document in collection "cities" with ID 'LA'
        let setDoc = db.collection('chatChannel').doc(channelName).set(data);
      }
      return 0
    });