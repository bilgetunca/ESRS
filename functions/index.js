const functions = require("firebase-functions/v1"); // v1 API zorlanıyor
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendEmergencyNotification = functions.firestore
  .document("emergencies/{emergencyId}")
  .onCreate(async (snap, context) => {
    console.log("sendEmergencyNotification fonksiyonu tetiklendi."); // 🔹 Bu satır eklendi

    const emergency = snap.data();
    const payload = {
      notification: {
        title: "Yeni Acil Durum",
        body: `${emergency.studentNo} numaralı öğrenciden yeni acil durum bildirimi.`,
        clickAction: "FLUTTER_NOTIFICATION_CLICK",
      },
    };

    try {
      const tokensSnapshot = await admin.firestore().collection("tokens").get();
      const tokens = tokensSnapshot.docs.map(doc => doc.id);
      if (tokens.length > 0) {
        const response = await admin.messaging().sendToDevice(tokens, payload);
        console.log("Bildirim gönderildi:", response);
      } else {
        console.log("Hiçbir token bulunamadı.");
      }
    } catch (error) {
      console.error("Bildirim gönderilemedi:", error);
    }
  });