const fs = require("fs");
const { deleteCollection, insert } = require('./helpers');
const firebase = require("firebase");
const { exit } = require("process");
const { info } = require("console");
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

const songs = JSON.parse(fs.readFileSync("./data/songs.json"));

(async () => {
    await deleteCollection(db, "collections");
    console.log("Xóa xong bảng collections");

    await deleteCollection(db, "songs");
    console.log("Xóa xong bảng songs");

    await insert(db, "collections", collections);
    console.info("Đã tạo collection 'collections' thành công");

    await insert(db, "songs", songs);
    console.info("Đã tạo collection 'songs' thành công");
    
    console.log("Hoàn tất");
})();
