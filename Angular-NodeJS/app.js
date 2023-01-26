'use strict';

const express = require('express');
const fetch = require('node-fetch');
const cors = require('cors');

const app = express();
app.use(cors());

const API_KEY = "YOUR_FINNHUB_API_KEY_HERE"

app.get('/', (req,res)=>{
    res.send('Stock App');
})

//COMPANY DESCRIPTION
app.get('/data/details/:stockTicker', async (req,res)=>{
    var ticker = req.params.stockTicker;
    console.log(`Getting data for ${ticker}`);
    // var details = await getStockDetails(req.params.stockTicker)

    var query = `https://finnhub.io/api/v1/stock/profile2?symbol=${ticker}&token=${API_KEY}`
    var headers = {'Content-Type': 'application/json'};

    const details = await fetch(query, {"method":"GET", "headers":headers})
        .then(res => res.json())
        .catch(e =>{
            console.log(`Error caught: ${e}`)
        })
    // var response = Object.assign({}, details, quote);    
    res.send(details);
})

//COMPANY HISTORICAL DATA
app.get('/data/hist/:stockTicker/date/:startDate/res/:resolve', async (req,res)=>{
    var ticker = req.params.stockTicker;
    var date = req.params.startDate;
    var resolution = req.params.resolve;
    console.log(`${ticker} in date: ${date}`);

    //resolution is D for 2 yrs of data graph
    //for summary chart we only need t (timestamp) and c(closed price) resolution is 5 difference should be 6 hrs
    var query = '';
    if(resolution=='D'){
        var priorDate = new Date(date*1000 - (2*365*24*60*60*1000))
        var priorString = `${priorDate.getTime()/1000}`
        query = `https://finnhub.io/api/v1/stock/candle?symbol=${ticker}&resolution=D&from=${priorString}&to=${date}&token=${API_KEY}`;
    }else{
        var priorTime = new Date(date*1000 - (6*60*60*1000))
        var priorTImeString = `${priorTime.getTime()/1000}`
        query = `https://finnhub.io/api/v1/stock/candle?symbol=${ticker}&resolution=5&from=${priorTImeString}&to=${date}&token=${API_KEY}`;
    }
    var headers = {'Content-Type': 'application/json'};
    const details = await fetch(query, {"method":"GET", "headers":headers})
        .then(res => res.json())
        .catch(e =>{
            console.log(`Error caught: ${e}`)
        })

        // console.log(query)
        if(resolution=='D'){
            res.send(histChartHelper(details));
        }else{
            res.send(summaryChartHelper(details));
        }
})

function histChartHelper(results){
    var new_array = [];
    for (let i=0; i<results.t.length; i++){
        var obj = {
            'c': results.c[i],
            'h': results.h[i],
            'l': results.l[i],
            'o': results.o[i],
            't': results.t[i],
            'v': results.v[i]
        }
        new_array.push(obj);
    }
    return new_array;
}

function summaryChartHelper(results){
    var new_array = [];
    for (let i=0; i<results.c.length; i++){
        var obj = {
            'closePrice': results.c[i],
            'timestamp': results.t[i]
        }
        new_array.push(obj)
    }
    return new_array;
}

//COMPANY LATEST PRICE
app.get('/data/price/:stockTicker', async (req,res)=>{
    var ticker = req.params.stockTicker;
    console.log(` Latest price for ${ticker}`);

    var query = `https://finnhub.io/api/v1/quote?symbol=${ticker}&token=${API_KEY}`
    var headers = {'Content-Type': 'application/json'};
    const details = await fetch(query, {"method":"GET", "headers":headers})
        .then(res => res.json())
        .catch(e =>{
            console.log(`Error caught: ${e}`)
        })
    var results = {
        "ticker": ticker.toUpperCase(),
        "currentPrice": details.c,
        "changePrice": details.d,
        "changePercent": details.dp,
        "highPrice": details.h,
        "lowPrice": details.l,
        "openPrice": details.o,
        "previousClose": details.pc,
        "timestamp": details.t
    }
    //need to send ticker along with price
    res.send(results);
})

//AUTOCOMPLETE
app.get('/data/search/:keyword', async (req, res)=>{
    var query = `https://finnhub.io/api/v1/search?q=${req.params.keyword}&token=${API_KEY}`
    var headers = {'Content-Type': 'application/json'};
    const details = await fetch(query, {"method":"GET", "headers":headers})
        .then(res => res.json())
        .catch(e =>{
            console.log(`Error caught: ${e}`)
        })
    var results = autoHelper(req.params.keyword, details.result)
    res.send(results);
})

function autoHelper(keyword, result){
    var new_array = [];
    for (let i=0; i<result.length; i++){
        var obj = result[i];
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
    return new_array
}


//COMPANY NEWS 
app.get('/data/news/:stockTicker', async (req,res)=>{
    var ticker = req.params.stockTicker

    var today = new Date();
    var todayString = `${today.getFullYear()}-${(today.getMonth()+1).toString().padStart(2,0)}-${today.getDate().toString().padStart(2,0)}`

    var last = new Date(today.getTime() - (7 * 24 * 60 * 60 * 1000));
    var lastString = `${last.getFullYear()}-${(last.getMonth()+1).toString().padStart(2,0)}-${last.getDate().toString().padStart(2,0)}`
    
    var query = `https://finnhub.io/api/v1/company-news?symbol=${ticker}&from=${lastString}&to=${todayString}&token=${API_KEY}` 
    var headers = {'Content-Type': 'application/json'};
    console.log(query);
    const details = await fetch(query, {"method":"GET", "headers":headers})
        .then(res => res.json())
        .catch(e =>{
            console.log(`Error caught: ${e}`)
        });
    var results = filterNews(details)
    res.send(results);

})

function filterNews(results){
    var new_array = [];
    for(let i=0; i<results.length; i++){
        var obj = results[i];
        if(new_array.length<20){
            if(obj.image!="" && obj.url!='' && obj.category!='' && obj.headline!='' && obj.summary!='' && obj.source!=''){
                
                var trialDate = obj.datetime;
                var date = new Date(trialDate*1000 - (7*60*60*1000))
                const months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December']
                var formattedDate = `${months[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`


                var new_obj = {
                    "category": obj.category,
                    "datetime": formattedDate,
                    "headline": obj.headline,
                    "image": obj.image,
                    "summary": obj.summary,
                    "url": obj.url,
                    "source": obj.source,
                }
                new_array.push(new_obj)
            }
        }
    }
    return new_array;
}

//COMPANY RECOMMENDATION TRENDS
app.get('/data/trends/:stockTicker', async (req,res)=>{
    var query = `https://finnhub.io/api/v1/stock/recommendation?symbol=${req.params.stockTicker}&token=${API_KEY}`
    var headers = {'Content-Type': 'application/json'};
    const details = await fetch(query, {"method":"GET", "headers":headers})
        .then(res => res.json())
        .catch(e =>{
            console.log(`Error caught: ${e}`)
        }) 
    res.send(details);
})


//COMPANY SOCIAL SENTIMENT
app.get('/data/social/:stockTicker', async (req,res) => {
    var ticker = req.params.stockTicker;

    var query = `https://finnhub.io/api/v1/stock/social-sentiment?symbol=${ticker}&from=2022-01-01&token=${API_KEY}`
    var headers = {'Content-Type': 'application/json'};
    const details = await fetch(query, {"method":"GET", "headers":headers})
        .then(res => res.json())
        .catch(e =>{
            console.log(`Error caught: ${e}`)
        }) 
    res.send(socialHelper(details));
})

function socialHelper(results){
    var posReddit = 0;
    var negReddit = 0;
    var posTwitter = 0;
    var negTwitter = 0;
    for(let i=0; i<results.reddit.length; i++){
        posReddit = posReddit + results.reddit[i].positiveMention
        negReddit = negReddit + results.reddit[i].negativeMention
    }
    for(let i=0; i<results.twitter.length; i++){
        posTwitter = posTwitter + results.twitter[i].positiveMention
        negTwitter = negTwitter + results.twitter[i].negativeMention
    }

    var new_obj = {
        "posReddit": posReddit,
        "negReddit": negReddit,
        "totalReddit": posReddit+negReddit,
        'posTwitter': posTwitter,
        'negTwitter': negTwitter,
        'totalTwitter': posTwitter+negTwitter
    }

    return new_obj
    
}

//COMPANY PEERS 
app.get('/data/peers/:stockTicker', async (req, res)=>{
    var query = `https://finnhub.io/api/v1/stock/peers?symbol=${req.params.stockTicker}&token=${API_KEY}`
    var headers = {'Content-Type': 'application/json'};
    const details = await fetch(query, {"method":"GET", "headers":headers})
        .then(res => res.json())
        .catch(e =>{
            console.log(`Error caught: ${e}`)
        }) 
    res.send(peerHelper(details));
})

function peerHelper(results){
    var new_array = [];
    for(let i=0; i<results.length; i++){
        var new_obj = {
            'peer': results[i]
        }
        new_array.push(new_obj)
    }
    return new_array;
}

//COMPANY EARNINGS
app.get('/data/earnings/:stockTicker', async (req, res)=>{
    var query = `https://finnhub.io/api/v1/stock/earnings?symbol=${req.params.stockTicker}&token=${API_KEY}`
    var headers = {'Content-Type': 'application/json'};
    const details = await fetch(query, {"method":"GET", "headers":headers})
        .then(res => res.json())
        .catch(e =>{
            console.log(`Error caught: ${e}`)
        }) 
    res.send(details);
})

// app.listen(8081, ()=>{
//     console.log('Server listening port 8081');
// })

// Start the server
const PORT = parseInt(process.env.PORT) || 8080;
app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});
// [END gae_node_request_example]

module.exports = app;
