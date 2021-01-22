const fs = require('fs');
const ZingMp3 = require('./lib/ZingMp3');
const { MIGRATION_PATH } = require('./config');

async function getTop100() {
    const zingChart = await ZingMp3.getDetailPlaylist("ZO68OC68");
    const songs = new Set();

    zingChart.song.items.map(song => ZingMp3.getFullInfo(song.encodeId))
        .forEach(async (promise) => {
            let song = await promise;

            songs.add({
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

            const json = JSON.stringify([...songs], null, 4);
            console.log("Đã lấy được: " + songs.size + " bài hát");

            if (!fs.existsSync(MIGRATION_PATH)) {
                fs.mkdirSync(MIGRATION_PATH);
            }

            fs.writeFileSync(`${MIGRATION_PATH}/songs.json`, json);
        });
}

(async () => {
    const response = await ZingMp3.getInfoArtist("Huy-Vac");
    console.log(response);
})();

