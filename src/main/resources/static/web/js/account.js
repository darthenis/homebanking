const {createApp} = Vue;


createApp({
    data(){

            return{
                accountData : null,
                isLoadingData: true,
                themeDark: localStorage.getItem('themeBH') === "dark" ? true : false || false,
                activeBar: null,
                clientName: localStorage.getItem("clientName"),
                isLoadingData: false
            }

    },
    created(){
        this.loadAccount(this.getParamFromUrl("id"));

    },
    methods : {
        getParamFromUrl(param){

            return new URLSearchParams(window.location.search).get(param);
        
        },
        loadAccount(id){
            this.isLoadingData = true;
            axios.get("http://localhost:8080/api/accounts/"+id)
                    .then(res => {

                        this.isLoadingData = false

                        let data = res.data;

                        data.transactions.sort((a, b) => b.id - a.id)

                        this.accountData = data;

                    })
        },
        
        toggleHandle(){

          this.themeDark = !this.themeDark;

          localStorage.setItem("themeBH", this.themeDark ? "dark" : "light");

        },
        getAnimationBar(barNum){

            if(!this.activeBar && this.activeBar !== null){
      
              return {'animation-name': `animatedBar${barNum}Reverse`};
      
            } else if(this.activeBar){
      
              return {'animation-name': `animatedBar${barNum}`};
      
            }
      
        },
        getAnimationNav(){

            if(!this.activeBar && this.activeBar !== null){
      
              return {'animation-name': `navAnimateReverse`};
      
            } else if(this.activeBar){
      
              return {'animation-name': `navAnimate`};
      
            }
      
          },
          formatDateAndTime(dateTime){
            
            let array = dateTime.split("T");
            let date = array[0].split("-").reverse().join("/");

            return date + " " + array[1].split(".")[0];

          },
          createChart(id){

            let newDataDebit = this.getLastTenMovements(id, "DEBIT");
      
            let newDataCredit = this.getLastTenMovements(id, "CREDIT");
      
            let options = {
              chart: {
                type: 'line',
                width: '150px',
                toolbar: {
                  show: false
                },
                animations: {
                  enabled: false,
                },
              },
              series: [{
                name: 'Debit',
                data: newDataDebit
              },
              {
                name: 'Credit',
                data: newDataCredit
              }],
              dataLabels:{
                distributed: false
              },
              xaxis: {
                categories: ["10", "9", "8", "7", "6", "5", "4", "3", "2", "1"],
                labels:{
                  show: false
                },
                axisBorder: {
                  show: false
                },  
                axisTicks:{
                  show: false
                }
              },
              yaxis: {
                show: false
              },
              grid: {
                show: false
              },
              colors: ['rgb(243, 0, 0)', 'rgb(0, 199, 0)'],
              dataLabels: {
                style: {
                  colors: ['#fffff', '#fffff', '#fffff']
                }
              },
              stroke: {
                show: true,
                curve: 'smooth',
                lineCap: 'butt',
                colors: undefined,
                width: 2,
                dashArray: 0,      
              },
              tooltip: {
                theme: "dark"
              },
              legend:{
                show: false
              }
            }
            
            const elementChart = document.querySelector("#chart"+id);
      
            if(elementChart){
      
              elementChart.innerHTML = "";
              
              const chart = new ApexCharts(elementChart, options);
            
              chart.render();
      
            }
      
          
          },

    }


}).mount("#app")