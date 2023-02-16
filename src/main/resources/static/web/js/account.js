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
        this.resizeEvent()
        window.addEventListener("resize", this.resizeEvent);
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
        resizeEvent(){
          this.resizeResetBarTogle();
          this.setIsMobile();
          
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
        formatDateAndTime(dateTime){
            
            let array = dateTime.split("T");
            let date = array[0].split("-").reverse().join("/");

            return date + " " + array[1].split(".")[0];

        },

    }


}).mount("#app")