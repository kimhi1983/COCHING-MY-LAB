


import DongCodeConvertor from "./DongCodeConvertor";
import {join} from 'path'


function doConverting(){

    console.debug(`__dirname:${__dirname}`);
    const rawFilePath = join(__dirname, '법정동코드 전체자료.txt');
    const destFilePath = join(__dirname, 'dongcode.json');
    console.debug(`rawFilePath:${rawFilePath}`);

    const dcc = new DongCodeConvertor();
    const data = dcc.convertToJson(rawFilePath, destFilePath);
    console.debug(data.sido[0]);
    console.debug(data.sigungu[0]);
}

doConverting();