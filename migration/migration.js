const firebase = require("firebase");
require("firebase/firestore");

const firebaseConfig = {
    apiKey: "AIzaSyB_uoTpYyjvh6358poZDR5A2bdhR5jqMvw",
    authDomain: "do-an-android-java.firebaseapp.com",
    databaseURL: "https://do-an-android-java-default-rtdb.firebaseio.com",
    projectId: "do-an-android-java",
    storageBucket: "do-an-android-java.appspot.com",
    messagingSenderId: "796931684678",
    appId: "1:796931684678:web:2bd3aa6adf3d92a1d0cd47",
    measurementId: "G-YQJEKJZMPZ"
};

firebase.initializeApp(firebaseConfig);

const db = firebase.firestore();

const albums = [
    {
        "name": "Rap Việt Ngày Nay",
        "description": "Chất hết mức với những bản Rap Việt ngày nay cùng Zing MP3",
        "image": "https://photo-zmp3.zadn.vn/banner/3/d/8/d/3d8dc525338137f8408bec2b2101f6e4.jpg",
        "songs": [
            "songs/KdESrxhJUFVpXWxthJyn"
        ],
        "recommend": true
    }
]

albums.forEach(function (album) {
    db.collection("albums").add({...album}).then(function (docRef) {
        console.log("Document written with ID: ", docRef.id);
    }).catch(function (error) {
        console.error("Error adding document: ", error);
    });
});