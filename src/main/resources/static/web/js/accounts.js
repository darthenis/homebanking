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
      balanceTotal: null,
      isLoadingData : false,
      filterAccounts: null,
      num1: 0,
      num2: 3
    };
  },
  created() {
    this.resizeEvent()
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

              client.accounts = client.accounts.map((account, index) => {

                if(index === 0){

                  return {...account, selected: true}

                } else {

                  return {...account, selected: false}

                }

              })

              this.selectedAccount = {...client.accounts[0]}
  
              this.clientData = client;

              this.filterAccounts = this.setInitialPageAccounts(this.clientData.accounts);
  
              this.clientData.accounts = this.clientData.accounts.map(account => {
  
                return {...account, firstPeriod: true}
  
              });
  
              this.balanceTotal = this.clientData.accounts.reduce((sum, account) => sum += account.balance, 0)
  
            })

    },
    resizeEvent(){
      this.resizeResetBarTogle();
      this.setIsMobile();
      if(!this.clientData) return;
      this.reloadPageAccounts();
      
    },
    checkActivedAccount(id){

          return this.filterAccounts.some(filterAcc => filterAcc.id === id)

    },
    getIdFromSelectedAccount(){

      let account = this.clientData.accounts.find(account => account.selected)

      return account.id;


    },
    getSelectedTransactions(){

      let account = this.clientData.accounts.find(account => account.selected)

      return [...account.transactions].splice(0, 5);


    },
    formatDateAndTime(dateTime){
            
      let array = dateTime.split("T");
      let date = array[0].split("-").reverse().join("/");

      return date + " " + array[1].split(".")[0];

    },
    setActivedFirstCard(){

      this.clientData.accounts =  this.clientData.accounts.map((account) => {

          if(account.id == this.filterAccounts[0].id) return {...account, selected: true}

          return {...account, selected : false}

      })

    },
    setInitialPageAccounts(accounts){

      let screenWidth = window.screen.width;

      if(screenWidth < 933 && screenWidth > 648){

        this.num2 = 2;

      } else if( screenWidth < 649 && screenWidth > 630){

        this.num2 = 1;

      } else if(screenWidth < 631 && screenWidth > 485){

          this.num2 = 2;

      }else if(screenWidth < 486){

        this.num2 = 1;

      } else {

        this.num2 = 3;

      }

      return accounts.slice(0, this.num2); 

    },
    reloadPageAccounts(){

      this.filterAccounts = this.setInitialPageAccounts(this.clientData.accounts)

      this.setActivedFirstCard()

    },
    handlePageAccounts(isPrev){

      let numOfElements = 3;

      if(this.isTablet && !this.ismobile){
      
        numOfElements = 2;

      } else if(this.isMobileSmall){

        numOfElements = 1;

      }

      if(isPrev){

        this.num1 -= numOfElements
        this.num2 -= numOfElements

      } else {

        this.num1 += numOfElements
        this.num2 += numOfElements

      }
     
      this.filterAccounts = this.clientData.accounts.slice(this.num1, this.num2); 

      this.setActivedFirstCard();
      
    },
    setIsMobile(){

      if(window.screen.width <= 646){

        this.isMobile = true;

      } else {

        this.isMobile = false;

      }

      if(window.screen.width <= 485){

        this.isMobileSmall = true;

      } else {

        this.isMobileSmall = false;

      }

      if(window.screen.width <= 933 && window.screen.width > 485){

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

      const transactions = this.clientData.accounts.find(acc => acc.id === id).transactions;

      transactions.sort((a, b) => b.id - a.id);

      let newData = [];

      let typeTransactions = transactions.filter(transaction => transaction.type === type);

      if(typeTransactions.length < 5){

        for(let transaction of typeTransactions){

          newData.push(transaction.amount);

        }

        return newData;

      } else {

        for(let i = typeTransactions.length - 1; i >= typeTransactions.length - 5; i--){

            newData.push(typeTransactions[i].amount);

        }

      }

      return newData;

   
    },

    createChart(id, selected){

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
          categories: ["5", "4", "3", "2", "1"],
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
          enabled: selected,
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

    reloadChart(){

      const account = this.clientData.accounts.find(account => account.selected);

      this.createChart(account)

    },

    selectAccount(id){

      this.clientData.accounts = this.clientData.accounts.map(account => {

              if(account.id === id) return {...account, selected : true}

              return {...account, selected : false}

      })


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
}).mount('#app')

