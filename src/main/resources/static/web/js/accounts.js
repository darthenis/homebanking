const { createApp } = Vue;

createApp({
  data() {
    return {
      clientData : null,
      isMobile: false,
      isMobileSmall: false,
      isTablet: false,
      activeBar: null,
      themeDark: localStorage.getItem('themeBH') === "dark" ? true : false || false,
      numbersAccounts: [],
      balanceTotal: null,
      isLoadingData : false,
      counterScroll: 0
    };
  },
  created() {
    window.addEventListener("resize", this.resizeEvent);
    this.loadData();
  },
  destroyed() {
    window.removeEventListener("resize", this.resizeEvent);
  },
  methods:{
    loadData(){

      this.isLoadingData = true;

      axios.get('http://localhost:8080/api/clients/1')
            .then(res => {
  
              localStorage.setItem("clientName", res.data.firstName + " " + res.data.lastName);

              this.isLoadingData = false;

              let client = res.data;

              client.accounts.sort((a, b) => a.id - b.id);

              this.selectedAccount = {...client.accounts[0]}
  
              this.clientData = client;
  
              this.clientData.accounts = this.clientData.accounts.map(account => {
  
                return {...account, firstPeriod: true}
  
              });
  
              this.numbersAccounts = res.data.accounts.map(account => account.number)
  
              this.balanceTotal = this.clientData.accounts.reduce((sum, account) => sum += account.balance, 0)
  
            })

    },
    resizeEvent(){
      this.resizeResetBarTogle();
      this.setIsMobile();
    },

    setIsMobile(){

      if(window.screen.width <= 631){

        this.isMobile = true;

      } else {

        this.isMobile = false;

      }

      if(window.screen.width <= 470){

        this.isMobileSmall = true;

      } else {

        this.isMobileSmall = false;

      }

      if(window.screen.width <= 933 && window.screen.width >= 470){

        this.isTablet = true;

      } else {

        this.isTablet = false;

      }

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
    resizeResetBarTogle(){

      this.activeBar = null;

    },

    getLastTenMovements(id, type){

      const transactions = this.clientData.accounts.find(acc => acc.id === id).transactions.sort((a, b) => a.id - b.id);

      let newData = [];

      let count = 0;

      for(let i = transactions.length - 1; i >= 0; i--){

          if(transactions[i].type === type){

            newData.push(transactions[i].amount);

            count++

          }

          if(count === 5){

            return newData;

          }

      }

      return newData.reverse();
   
    },

    handlerCards(isNext){

      if(isNext){

        this.counterScroll += 1;

        const article = document.getElementById('article'+ (this.counterScroll));

        this.scrollTo(article)

    
      } else {

        this.counterScroll -= 1;

        const article = document.getElementById('article'+ (this.counterScroll));

        this.scrollTo(article)

       

      }

    },
    
    scrollTo(element){

      element.parentNode.scrollLeft = element.offsetLeft;
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

    createCarrousel(){

      new Splide( '.splide',{
        type: "slice",
        perPage: 3,
        pagination: false,
        width: "70vw",
        arrows: { prev: document.getElementById("btn__prev"), next: document.getElementById("btn__mext") }

      }).mount();

    },

    disableScroll(disabled){

      if(!disabled) document.body.style.overflow = 'hidden';

      else document.body.style.overflow = 'auto';

    },

    formateDateNotYear(date){

     let array = date.split("-").reverse()

     return array[0] + "/" + array[1]

    }
  },
  mounted(){

    this.resizeEvent();


  }
}).mount("#app");
