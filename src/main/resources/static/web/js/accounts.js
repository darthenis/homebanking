const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      clientData : null,
      isMobile: false,
      isMobileSmall: false,
      isTablet: false,
      activeBar: null,
      accountType: "",
      themeDark: true,
      balanceTotal: null,
      isLoadingData : false,
      isLoading: false,
      selectedAccoutId : null,
      activeModalConfirm: false,
      activeModalAccountType : false,
      modalMessage : {
        message: "",
        isError: false
      }
    };
  },
  created() {
    this.resizeEvent()
    window.addEventListener("resize", this.resizeEvent);
    this.loadData();
  },
  destroyed(){

    window.removeEventListener("resize", this.resizeEvent);
    this.resizeEvent()
  },
  methods:{
    loadData(){

      this.isLoadingData = true;


      axios.get('/api/clients/current')
            .then(res => {

              this.isLoadingData = false;

              localStorage.setItem("clientName", res.data.firstName + " " + res.data.lastName);

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
  
              this.clientData.accounts = this.clientData.accounts.map(account => {
  
                return {...account, firstPeriod: true}
  
              });
  
              this.balanceTotal = this.clientData.accounts.reduce((sum, account) => sum += account.balance, 0)

              this.selectedAccoutId = this.clientData.accounts[0]?.id || null;

              localStorage.setItem("selectedAccount", this.clientData.accounts[0]?.id || null)

              localStorage.setItem("clientData", JSON.stringify({ firstName : this.clientData.firstName, 
                                                                lastName : this.clientData.lastName,
                                                                avatarUrl : this.clientData.avatarUrl}))

            })

    },
    desactiveModal(){

      this.activeModalAccountType = false;
      this.activeModalConfirm = false;

    },
    logout(){

      axios.post('/api/logout').then(response => {
        
        localStorage.removeItem('clientData')
        
        window.location.href = '/web/index.html'
      
      })

    },
    resizeEvent(){
      this.resizeResetBarTogle();
      this.setIsMobile();
      if(!this.clientData) return;
      
    },
    messageAlertHandler(message, seconds, isError){

      this.modalMessage.message = message;

      this.modalMessage.isError = isError;

      setTimeout(() => this.modalMessage.message = "", seconds * 1000)

    },
    getSelectedTransactions(){

      let account = this.clientData?.accounts.find(account => account.selected)

      if(!account) return []; 

      return [...account.transactions].splice(0, 5);


    },
    formatDateAndTime(dateTime){
            
      let array = dateTime.split("T");
      let date = array[0].split("-").reverse().join("/");

      return date + " " + array[1].split(".")[0];

    },
    deleteAccount(){

      this.isLoading = true;

      axios.delete('/api/clients/current/accounts/'+ this.selectedAccoutId)
          .then(res => {

            this.isLoading = false;
            this.loadData()
            this.activeModalConfirm = false;
            this.messageAlertHandler("Account deleted successfully", 3, false)

          }).catch(err => {

            console.log(err)
            this.isLoading = false;
            this.activeModalConfirm = false;
            this.messageAlertHandler(err.response.data, 3, true)

          })

    },
 
    setIsMobile(){

      if(window.innerWidth <= 646){

        this.isMobile = true;

      } else {

        this.isMobile = false;

      }

      if(window.innerWidth <= 485){

        this.isMobileSmall = true;

      } else {

        this.isMobileSmall = false;

      }

      if(window.innerWidth <= 933 && window.innerWidth > 485){

        this.isTablet = true;

      } else {

        this.isTablet = false;

      }

    },

    handleScroll(isNext){

      const container = document.getElementById('container__cards');

      if(isNext) container.scrollLeft += container.scrollWidth / this.clientData.accounts.length;

      else container.scrollLeft -= container.scrollWidth / this.clientData.accounts.length;

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

    getLastTenMovements(id){

      const transactions = this.clientData.accounts.find(acc => acc.id === id).transactions;

      transactions.sort((a, b) => b.id - a.id);

      let newData = [];

      if(transactions.length < 5){

        for(let transaction of transactions){

          newData.push(transaction.amount);

        }

        return newData;

      } else {

        for(let i = transactions.length - 1; i >= transactions.length - 5; i--){

            newData.push(transactions[i].amount);

        }

      }

      return newData;

   
    },

    createChart(id, selected){

      let newDataDebit = this.getLastTenMovements(id);

      console.log('chart: ', newDataDebit, ' id ', id)

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
          name: 'movements',
          data: newDataDebit.length ? newDataDebit : [0,0,0,0,0]
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
        colors: [newDataDebit[0] < newDataDebit[4] ? 'rgb(0, 199, 0)' : 'rgb(243, 0, 0)'],
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

    selectAccount(id){

      this.clientData.accounts = this.clientData.accounts.map(account => {

              if(account.id === id) return {...account, selected : true}

              return {...account, selected : false}

      })


      localStorage.setItem('selectedAccount', id);

      this.selectedAccoutId = id;

    },
    
    ifActiveLoan(loanName){

      return this.clientData?.loans.some(transaction => transaction.name === loanName);

    },

    disableScroll(disabled){

      if(!disabled) document.body.style.overflow = 'hidden';

      else document.body.style.overflow = 'auto';

    },

    formateDateNotYear(date){

     let array = date.split("-").reverse()

     return array[0] + "/" + array[1]

    },
    createAccount(){

      axios.post('/api/clients/current/accounts', `accountType=${this.accountType.toUpperCase()}`)
          .then(res => {

            this.messageAlertHandler("Account created successfully", 3, false)

            this.loadData();

            this.desactiveModal();

          }).catch(err => {

            this.messageAlertHandler(err.response.data, 3, true)

            this.desactiveModal();

          })

    },
    formatMoney(money){

      const formatter = new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
      
      });
      
      return formatter.format(money)

    },
    confirmModal(){

      if(this.activeModalConfirm){

        this.deleteAccount();

      } else {

        this.createAccount();

      }
      

    },
    getNumberSelectedAccount(){

      return this.clientData.accounts.find(account => account.id === this.selectedAccoutId).number;

    }
  },
  mounted(){

    this.resizeEvent();

  }
})

app.mount("#app")

