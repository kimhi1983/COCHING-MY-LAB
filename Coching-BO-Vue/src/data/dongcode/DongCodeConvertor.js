

import fs from 'fs';
 
class DongCodeConvertor{
    
    convertToJson(strFilePath, strDestPath){
        const rawCont = fs.readFileSync(strFilePath, 'utf8');
        //console.debug(`rawCont:`);
        //console.debug(rawCont);

        const lines = rawCont.split('\n');
        if(lines < 2){
            return [];
        }

        const retVal = {
            sido : [], 
            sigungu : []
        }

        for(const [idx, line] of Object.entries(lines)){
            if(idx < 1){
                continue; //Header skip
            }
            const data = line.split("\t");

            if(data.length < 3 || '' == data[0] || data[0].length != 10){
                continue; //skip
            }

            const d = {
                code : data[0],
                name : data[1]
            };

            d.name = d.name.replace("직할시", "광역시");

            if(d.code.indexOf("00000000") == 2){
                retVal.sido.push(d);
            }else if(d.code.substring(5) == "00000"){
                retVal.sigungu.push(d);
            }
        }

        const strData = JSON.stringify(retVal);
        fs.writeFileSync(strDestPath, strData);

        return retVal;
    }
}

export default DongCodeConvertor;