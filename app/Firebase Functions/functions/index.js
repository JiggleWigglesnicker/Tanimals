const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

exports.modifyUser = functions.firestore
    .document('match/{userId}')
    .onWrite((change, context) => {

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
          [userId]: true,
        };
        
        const channelRef = db.collection('chatChannel').doc(channelName)

        let getDoc = channelRef.get()
          .then(doc => {
            if (!doc.exists) {
              let setDoc = channelRef.set(data);
            } else {
              let updateSingle = channelRef.update({[userId]: true});
            }
            return null
          })
          .catch(err => {
            console.log('Error getting document', err);
          });

      }
      return 0
    });