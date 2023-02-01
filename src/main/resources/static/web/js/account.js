const { createApp } = Vue;

createApp({
  data() {
    return {
      clientData : null,
      accountData : undefined,
      isMobile: false,
      activeBar: null,
      accountsShowed: [],
      selectedAccount: null,
      themeDark: localStorage.getItem('themeBH') === "dark" ? true : false || false,
      numbersAccounts: [],
      balanceTotal: null,
    };
  },
  created() {
    window.addEventListener("resize", this.resizeEvent);
    axios.get('http://localhost:8080/api/client/1')
          .then(res => {

            this.clientData = res.data;

            this.clientData.accounts = this.clientData.accounts.map(account => {

              return {...account, firstPeriod: true}

            });

            this.clientData.accounts.sort((a, b) => a.id - b.id);

            this.accountsShowed.push({...this.clientData.accounts[0]});
            this.accountsShowed.push({...this.clientData.accounts[1]});

            this.numbersAccounts = res.data.accounts.map(account => account.number)

            this.balanceTotal = this.clientData.accounts.reduce((sum, account) => sum += account.balance, 0)

          })
  },
  destroyed() {
    window.removeEventListener("resize", this.resizeEvent);
  },
  methods:{
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

    },

    toggleHandle(){

      this.themeDark = !this.themeDark;

      localStorage.setItem("themeBH", this.themeDark ? "dark" : "light");

    },

    toggleAccountsShowed(direction){

      let firstIndex = this.clientData.accounts.findIndex(account => account.id === this.accountsShowed[0].id);

      let secondIndex = this.clientData.accounts.findIndex(account => account.id === this.accountsShowed[1].id);

      if(direction === "left" && firstIndex !== 0){

        this.accountsShowed[0] = {...this.clientData.accounts[firstIndex - 2]};

        this.accountsShowed[1] = {...this.clientData.accounts[secondIndex - 2]};

      } else if(direction === "right" && secondIndex !== this.clientData.accounts.length - 1){

        this.accountsShowed[0] = {...this.clientData.accounts[firstIndex + 2]};

        this.accountsShowed[1] = {...this.clientData.accounts[secondIndex + 2]};

      }


    },

    selectAccount(account){

      this.selectedAccount = {...account};

      this.disableScroll(false);

    },

    unselectAccount(){

      this.selectedAccount = null;

      this.disableScroll(true);


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
    getAnimationMovement(){

      if(!this.selectedAccount){

        return {'animation-name': `modal__movement__slideout`};

      } else if(this.selectedAccount){

        return {'animation-name': `modal__movement__slide`};

      }

    },
    resizeResetBarTogle(){

      this.activeBar = null;

    },

    getLastSixMonth(){

      const monthsNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

      let today = new Date();
      let d;
      let months = { names: [], numbers: []};

      for(var i = 5; i >= 0; i -= 1) {
        d = new Date(today.getFullYear(), today.getMonth() - i, 1);
        months.names.push(monthsNames[d.getMonth()]);
        months.numbers.push(today.getMonth() - i);
      }

      return months

    },

    getMovementsLastSixMonth(index){

      let months = this.getLastSixMonth();

      let dateCreation = this.accountsShowed[index].creationDate.split("-")

      let newData = [];

      for(let [idx, number] of months.numbers.entries()){

        if(dateCreation[1] - 1 <= number){

          newData.push(this.accountsShowed[index].balance)

        } else {

          newData.push(0)

        }

      }

      return newData;

    },

    createChart(index){

      let monthsNames = this.getLastSixMonth().names;

      let newData = this.getMovementsLastSixMonth(index);

      let options = {
        chart: {
          type: 'line',
          toolbar: {
            show: false
          },
          animations: {
            enabled: false,
          },
        },
        series: [{
          name: 'balance',
          data: newData
        }],
        xaxis: {
          categories: monthsNames,
          tickPlacement: 'between',
          labels:{
            style:{
              colors: this.themeDark ? "#ffffff" : "#111",
              cssClass: "labelChart"
            }
          },
          axisBorder: {
            width: '300px'
          },
        },
        yaxis: {
          labels: {
            style: {
              colors: this.themeDark ? "#ffffff" : "#111",
            }
          }
        },
        grid: {
          yaxis: {
            lines: {
              show: false
            }
          }
        },
        colors:['rgb(0, 199, 0)', '#E91E63', '#9C27B0'],
        dataLabels: {
          style: {
            colors: ['#fffff', '#fffff', '#fffff']
          }
        },
        tooltip: {
          theme: "dark"
        }
      }
      
      const chart = new ApexCharts(document.querySelector("#chart"+(index + 1)), options);
      
      chart.render();
    },

    formatDate(date){

      return date.split("-").reverse().join("/");

    },

    disableScroll(disabled){

      if(!disabled) document.body.style.overflow = 'hidden';

      else document.body.style.overflow = 'auto';

    },

    formateDateNotYear(date){

     let array = date.split("-").reverse()

     return array[0] + "/" + array[1]

    },

    renderCharts(){

      if(!this.clientData || !this.accountsShowed.length) return;

      const chart1 = document.querySelector("#chart1");

      const chart2 = document.querySelector("#chart2");

      if(chart1) chart1.innerHTML = "";

      if(chart2) chart2.innerHTML = "";

      this.createChart(0);
      this.createChart(1);

    }
  },
  computed:{

      reLoadData(){

        this.renderCharts();

      }

  },
  mounted(){

    this.resizeEvent();
    this.renderCharts();

  }
}).mount("#app");
