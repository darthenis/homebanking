const {createApp} = Vue;


createApp({
    data(){

            return{
                clientData : null,
                transactions : null,
                filteredTransactions: null,
                isLoadingData: true,
                themeDark: true,
                activeBar: null,
                clientName: localStorage.getItem("clientName"),
                isLoadingData: false,
                isMobile: false,
                numPage: 0,
                datesBetween : null,
                dateFrom : null,
                dateThru: null,
                arrowFilterActive : {
                  column : "",
                  direction: 0
                }
            }

    },
    created(){
        this.resizeEvent()
        window.addEventListener("resize", this.resizeEvent);
        this.loadAccount(this.getParamFromUrl("id"));
        this.clientData = JSON.parse(localStorage.getItem('clientData'));
     

    },
    destroyed(){
      window.addEventListener("resize", this.resizeEvent);

    },
    methods : {
        getParamFromUrl(param){

            return new URLSearchParams(window.location.search).get(param);
        
        },
        getPages(){

          if(!this.transactions) return;

          let numPages = this.transactions.length / 10;

          return (this.numPage / 10 + 1) + ' of ' + numPages + ' pages';

        },
        setDataTransactions(transactions){

          console.log(transactions)

          let array = [...transactions];

          if(!array.length) {

            this.filteredTransactions = [];

            this.transactions = [];

            return;

          }

          array = array.map(transaction => {

            return {...transaction, date : this.formatDateOrTime(transaction.date, false), hour : this.formatDateOrTime(transaction.date, true)}

          })

          array.sort((a, b) => b.id - a.id)

          this.filteredTransactions = [...array.slice(0, 10)];

          this.transactions = [...array];

        },
        loadAccount(id){
            this.isLoadingData = true;
            axios.get("/api/clients/current/accounts/"+id+"/transactions")
                    .then(res => {

                        this.isLoadingData = false

                        this.setDataTransactions(res.data);

                    })
        },
        setOrderColumn(columnName){

          if(this.arrowFilterActive.column !== columnName){

            this.arrowFilterActive.column = columnName;

            this.arrowFilterActive.direction = 1;

          } else {

            if(this.arrowFilterActive.direction < 2) this.arrowFilterActive.direction += 1;

            else {
              
              this.arrowFilterActive.column = "";

              this.arrowFilterActive.direction = 0;
            
            }

          }

          this.filterByOrder();

        },
        filterByOrder(){

          if(this.arrowFilterActive.direction == 1) {
            
            this.transactions.sort((a, b) => {

                                                let aAux = a[this.arrowFilterActive.column.toLowerCase()];

                                                let bAux = b[this.arrowFilterActive.column.toLowerCase()];

                                                if(this.arrowFilterActive.column === "Hour"){

                                                  let timeA = new Date();
                                                    
                                                  timeA.setHours(aAux.split(":")[0],aAux.split(":")[1],aAux.split(":")[2]);
                                                  
                                                  let timeB = new Date();
                                                  
                                                  timeB.setHours(bAux.split(":")[0],bAux.split(":")[1],bAux.split(":")[2]);

                                                  if ( timeA < timeB){
                                                    
                                                    return -1;

                                                  }

                                                  if (timeA > timeB) {
                                                            return 1;
                                                        }
                                                              return 0;

                                                } else if(this.arrowFilterActive.column === "Date"){
                                                  
                                                  if ( new Date(aAux).getTime() < new Date(bAux).getTime()){
                                                    
                                                    return -1;

                                                  }

                                                  if (new Date(aAux).getTime() > new Date(bAux).getTime()) {
                                                            return 1;
                                                        }
                                                              return 0;


                                                } else {

                                                  if ( a[this.arrowFilterActive.column.toLowerCase()] < b[this.arrowFilterActive.column.toLowerCase()]){
                                                    
                                                    return -1;

                                                  }

                                                  if (a[this.arrowFilterActive.column.toLowerCase()] > b[this.arrowFilterActive.column.toLowerCase()]) {
                                                            return 1;
                                                        }
                                                              return 0;


                                                }

                                              });}
          
          else if(this.arrowFilterActive.direction == 2) this.transactions.sort((a, b) => {

                  let aAux = a[this.arrowFilterActive.column.toLowerCase()];

                  let bAux = b[this.arrowFilterActive.column.toLowerCase()];

                  if(this.arrowFilterActive.column == "Date"){
                    
                    if ( new Date(aAux).getTime() < new Date(bAux).getTime()){
                      
                      return 1;

                    }

                    if (new Date(aAux).getTime() > new Date(bAux).getTime()) {
                              return -1;
                          }
                                return 0;


                  } else {

                    if ( a[this.arrowFilterActive.column.toLowerCase()] < b[this.arrowFilterActive.column.toLowerCase()]){
                      
                      return 1;

                    }

                    if (a[this.arrowFilterActive.column.toLowerCase()] > b[this.arrowFilterActive.column.toLowerCase()]) {
                              return -1;
                          }
                                return 0;


                  }

                }) 
                
        else this.transactions.sort((a, b) =>  b.id - a.id);

        this.filteredTransactions = [...this.transactions.slice(this.numPage, this.numPage + 10)]

        },

        movePage(isNext){

          if(isNext && this.numPage < this.transactions.length - 11){

            this.numPage += 10;

          } else if(!isNext && this.numPage > 0) {

            this.numPage -= 10;

          }

          this.filteredTransactions = [...this.transactions.slice(this.numPage, this.numPage + 10)]

        },

        resizeEvent(){
          this.resizeResetBarTogle();
          this.setIsMobile();
          
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
        logout(){

          axios.post('/api/logout').then(response => {
            
            localStorage.removeItem('clientData')
            
            window.location.href = '/web/index.html'
          
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
        resizeResetBarTogle(){

          this.activeBar = null;
    
        },
        formatDateOrTime(dateTime, returnTime){

          let array = dateTime.split("T");

          let date = array[0].split("-").reverse().join("/");

          if(returnTime){

            return array[1].split(".")[0];

          } else {

            return date;

          }
            
        },
        formatMoney(money){

          const formatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
          
          });
          
          return formatter.format(money)
    
        },
        printDatePicker(){

          const element = document.getElementById("dateInput");

          flatpickr(element, {
                              mode: "range",
                              enableTime: true,
                              dateFormat: "Y-m-d H:i",})

        },

        checkDates(){

          let divideDates = this.datesBetween.split("to");

          this.dateFrom = new Date(divideDates[0].trim());

          if(divideDates.length > 1){

            this.dateThru = new Date(divideDates[1].trim());

          } else {

            this.dateThru = null;

          }

        },
        getPdf(){

          let dateFrom = this.dateFrom ? new Date(this.dateFrom) : null;

          let dateThru = this.dateThru ? new Date(this.dateThru) : null;

          return `/api/clients/current/accounts/${this.getParamFromUrl("id")}/transactions/pdf?${dateFrom !== null ? "dateFrom=" + dateFrom.toJSON() : "" }${dateThru !== null ? "&dateThru=" + dateThru.toJSON() : ""}`
        },
        filterByDate(){

            let dateFrom = new Date(this.dateFrom);

            let dateThru = new Date(this.dateThru);

            axios.get(`/api/clients/current/accounts/${this.getParamFromUrl("id")}/transactions?dateFrom=${dateFrom.toJSON()}&dateThru=${dateThru.toJSON()}`)
                  .then(res => {

                    console.log('result: ', res)

                    this.setDataTransactions(res.data)

                  })
                  .catch(err => {

                    console.log(err)

                  })

        }
      },
      mounted() {
        window.addEventListener('resize', this.resizeEvent);
        this.printDatePicker()
      },
      unmounted() {
        window.removeEventListener('resize', this.resizeEvent);
      },

}).mount("#app")