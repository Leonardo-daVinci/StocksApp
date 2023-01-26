var mockdata = 
[{"description":"VOYAGEUR PHARMACEUTICALS LTD","displaySymbol":"VM.V","symbol":"VM.V","type":"Common Stock"},{"description":"VOYAGEUR PHARMACEUTICALS LTD","displaySymbol":"VM.NE","symbol":"VM.NE","type":"Common Stock"},{"description":"VMWARE INC-CLASS A","displaySymbol":"VMW","primary":["VMWA.VI","BZF1.SG","BZF1.DU","BZF1.MU","0LQO.L","BZF1.BE","V2MW34.SA","VMW.MX","BZF1.DE","BZF1.F"],"symbol":"VMW","type":"Common Stock"},{"description":"VALMONT INDUSTRIES","displaySymbol":"VMI","primary":["VI1.F","VI1.SG","VI1.BE"],"symbol":"VMI","type":"Common Stock"},{"description":"VULCAN MATERIALS CO","displaySymbol":"VMC","primary":["V1MC34F.SA","V1MC34.SA","VMC.MX","VMC.BE","VMC.F","VMC.SG","VMC.DU","0LRK.L","VMC-RM.ME"],"symbol":"VMC","type":"Common Stock"},{"description":"INVESCO MUNICIPAL OPPORTUNI","displaySymbol":"VMO","primary":null,"symbol":"VMO","type":"Closed-End Fund"},{"description":"VIEMED HEALTHCARE INC","displaySymbol":"VMD","primary":null,"symbol":"VMD","type":"Common Stock"},{"description":"EVMO INC","displaySymbol":"YAYO","symbol":"YAYO","type":"Common Stock"},{"description":"AVANTIUM","displaySymbol":"AVTXF","symbol":"AVTXF","type":"Common Stock"},{"description":"VMware","displaySymbol":"BZFA.MU","symbol":"BZFA.MU","type":""},{"description":"VMware","displaySymbol":"A3KUFY.MU","symbol":"A3KUFY.MU","type":""},{"description":"VMware","displaySymbol":"A3KUFW.MU","symbol":"A3KUFW.MU","type":""},{"description":"VMware","displaySymbol":"BZFC.MU","symbol":"BZFC.MU","type":""},{"description":"VMware","displaySymbol":"A3KUFZ.MU","symbol":"A3KUFZ.MU","type":""},{"description":"Devoteam","displaySymbol":"0E7S.L","symbol":"0E7S.L","type":""},{"description":"Nova KBM","displaySymbol":"A3K0PS.BE","symbol":"A3K0PS.BE","type":""},{"description":"Nova KBM","displaySymbol":"A3K0PS.MU","symbol":"A3K0PS.MU","type":""},{"description":"Nova KBM","displaySymbol":"A3K0PS.DU","symbol":"A3K0PS.DU","type":""},{"description":"VTM LTD","displaySymbol":"532893.BO","symbol":"532893.BO","type":"Common Stock"},{"description":"Nova KBM","displaySymbol":"A3K0PS.HM","symbol":"A3K0PS.HM","type":""},{"description":"Nova KBM","displaySymbol":"A3K0PS.HA","symbol":"A3K0PS.HA","type":""},{"description":"VEEM LTD","displaySymbol":"VEE.AX","symbol":"VEE.AX","type":"Common Stock"},{"description":"ViacomCBS","displaySymbol":"A28VBM.MU","symbol":"A28VBM.MU","type":""},{"description":"AVANTIUM","displaySymbol":"27V.F","symbol":"27V.F","type":"Common Stock"},{"description":"VOTUM SA","displaySymbol":"0Q6K.L","symbol":"0Q6K.L","type":"Common Stock"},{"description":"VIPOM AD","displaySymbol":"0O8U.L","symbol":"0O8U.L","type":"Common Stock"},{"description":"AVANTIUM","displaySymbol":"27V.SG","symbol":"27V.SG","type":"Common Stock"},{"description":"VOTUM SA","displaySymbol":"VOT.WA","symbol":"VOT.WA","type":"Common Stock"},{"description":"AVANTIUM","displaySymbol":"27V.MU","symbol":"27V.MU","type":"Common Stock"}]

var keyword = "VM"
var new_array = [];

for (let i=0; i<mockdata.length; i++){
    var obj = mockdata[i];
    if(obj.type == "Common Stock"){
        if(!obj.symbol.includes(".")){
            if(obj.description.includes(keyword) || obj.symbol.includes(keyword)){
                var new_obj = {
                    "symbol": obj.symbol,
                    "description": obj.description,
                }
                new_array.push(new_obj)
            }
        }
    }
}


// console.log(new_array)

var trialDate = 1585723096;
var date = new Date(trialDate*1000)
const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December']
var formattedDate = `${months[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`
// console.log(formattedDate)
// console.log(Math.floor(Date.now()/1000));

var priorDate = new Date(trialDate*1000 - (2*365*24*60*60*1000))
// console.log(priorDate.getTime()/1000)

var stringDate = "2021-12-31"
console.log(Date.parse(stringDate))
