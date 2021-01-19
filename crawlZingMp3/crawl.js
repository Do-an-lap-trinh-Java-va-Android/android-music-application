const _ = require("lodash");
const fs = require('fs');
const ZingMp3 = require('./lib/ZingMp3');

(async () => {
    const zingChart = await ZingMp3.getDetailPlaylist("ZO68OC68");
    const top100 = new Set();

    let mapToPromise = zingChart.song.items.map(song => ZingMp3.getFullInfo(song.encodeId))

    mapToPromise.forEach(async (promise) => {
        let song = await promise;

        top100.add({
            name: song.title,
            artists: song.artists.map(artist => artist.name),
            thumbnail: song.thumbnail.replace('w94', 'w450'),
            listens: song.listen,
            like: song.like,
            year: new Date(song.releaseDate * 1000).getFullYear(),
            mp3: song.streaming['128'],
            albums: [],
            duration: song.duration
        });

        // Cập nhật vào tệp json sau mỗi 20 bài
        if ((top100.size % 20) === 0) {
            const merger = JSON.stringify([...top100], null, 4);
            console.log("Đã lấy được: " + top100.size + " bài hát");
            fs.writeFileSync("./songs.json", merger);
        }
    });
})();
