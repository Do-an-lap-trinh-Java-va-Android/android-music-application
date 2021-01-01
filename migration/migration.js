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

const collections = [
    {
        "name": "Album Slider",
        "albums": [
            {
                "name": "Rap Việt Ngày Nay",
                "description": "Chất hết mức với những bản Rap Việt ngày nay cùng Zing MP3",
                "cover": "https://photo-zmp3.zadn.vn/banner/3/d/8/d/3d8dc525338137f8408bec2b2101f6e4.jpg"
            },
            {
                "name": "Tay Nắm Tay Rời",
                "description": "Phạm Đình Thái Ngân và ca khúc dành cho những mối tình dở dang, chưa kịp nở đã vội úa tàn",
                "cover": "https://photo-zmp3.zadn.vn/banner/5/c/1/4/5c147edff6eab95cf4f35af01fd7a5dc.jpg"
            },
            {
                "name": "New Year Party",
                "description": "Những ca khúc sôi động nhất dành cho các buổi tiệc, chắc chắn bạn không thể ngừng nhún nhẩy theo",
                "cover": "https://photo-zmp3.zadn.vn/banner/d/9/d/b/d9dbd5ec33e60494982a0608744b0c49.jpg"
            }
        ]
    },
];

const songs = [
    {
        "name": "Anh Ơi Ở Lại",
        "other_name": "Cám Tấm",
        "thumbnail": "https://photo-resize-zmp3.zadn.vn/w320_r1x1_jpeg/cover/3/f/b/e/3fbe71079a3c00cfeffc92de527571c7.jpg",
        "views": 0,
        "artists": ["Chi Pu"],
        "albums": [],
    },
    {
        "name": "Chắc Ai Đó Sẽ Về",
        "other_name": "",
        "thumbnail": "https://photo-resize-zmp3.zadn.vn/w320_r1x1_jpeg/cover/0/6/0/4/0604b2039e6be2b7c8d4f3243b24594d.jpg",
        "views": 0,
        "artists": ["Sơn Tùng MTP"],
        "albums": [],
    },
    {
        "name": "How You Like That",
        "other_name": "",
        "thumbnail": "https://photo-resize-zmp3.zadn.vn/w94_r1x1_jpeg/cover/0/0/4/1/0041083628270504efdb6499396aacea.jpg",
        "views": 0,
        "artists": ["BLACKPINK"],
        "albums": [],
    }
];

db.collection("collections").get().then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
        doc.ref.delete();
    });
});

db.collection("songs").get().then(function(querySnapshot) {
    querySnapshot.forEach(function(doc) {
        doc.ref.delete();
    });


    collections.forEach(function (collection) {
        db.collection("collections").add({...collection}).then(function (docRef) {
            console.log("Đã thêm collection: ", docRef.id);
        }).catch(function (error) {
            console.error("Không thể thêm collection: ", error);
        });
    });
    
    songs.forEach(function (song) {
        db.collection("songs").add({...song}).then(function (docRef) {
            console.log("Đã thêm bài hát: ", docRef.id);
        }).catch(function (error) {
            console.error("Không thể thêm bài hát: ", error);
        });
    });
});

