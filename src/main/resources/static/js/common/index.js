  	// 날씨 영역
  	var apiURI = "http://api.openweathermap.org/data/2.5/weather?q="+"seoul"+ "&lang=kr" + "&units=metric" + 
  			"&appid="+"be9d839b0ff3435abb7f4e732bcc8649";

    const weatherMap = {
        'Clear': '맑음',
        'Clouds': '구름',
        'Rain': '비',
        'Drizzle': '이슬비',
        'Thunderstorm': '천둥번개',
        'Snow': '눈',
        'Mist': '안개',
        'Smoke': '연기',
        'Haze': '실안개',
        'Dust': '먼지',
        'Fog': '안개',
        'Sand': '모래',
        'Ash': '재',
        'Squall': '돌풍',
        'Tornado': '토네이도'
    };
 	
  	$.ajax({
  	    url: apiURI,
  	    dataType: "json",
  	    type: "GET",
  	    success: function(data) {
  	        var weatherDesc = data.weather[0].main;
  	        var temp = (data.main.temp).toFixed(1);
  	        var city = '서울';

  	      	$('#weather-icon').append('<img src= "http://openweathermap.org/img/wn/'
  	              + data.weather[0].icon + '.png"></img>');
  	        $('#weather-desc').append(weatherMap[weatherDesc]);
  	        $('#temp').append(temp + "°C");
  	        $('#city').append(city);
  	    }
  	})
  	
	// 달력 영역
	  const calendarEl = document.getElementById('calendar');
	  const calendarHeader = {
	    left: 'prev,next',          
	    center: 'title',
	    right:'today'        
	  }
	
	  const calendarOpt = {
	    headerToolbar: calendarHeader,
	    initialView: 'dayGridMonth',
	    selectable:true,    // 달력 날짜 드래그
	    locale:'kr',      // 달력 언어 설정
	    dayMaxEventRows:true
	  }
	
	  const calendar = new FullCalendar.Calendar(calendarEl, calendarOpt);
	  calendar.render();
	  
	  // 출 퇴근 영역
	  let startWorkTime, endWorkTime, workingTime;
	  let timer;
  	  let hour = 0, minute = 0, second = 0;
  	  let perWork = 0;
  	  let h = 0, m = 0, s = 0;
	  function updateClock() {
		  	++second;
		  	++perWork;
		  	
		  	if(second >= 60){
		  		++minute;
		  		second = 0;
		  	}
		    if(minute >= 60){ 
		        ++hour;
		        minute = 0;
		    }
		    // 숫자를 2자리로 표현
		    h = String(hour).padStart(2, '0');
		    m = String(minute).padStart(2, '0');
		    s = String(second).padStart(2, '0');
		    
		    let perval = ((perWork / 32400) * 100);
		    
		    $(".progress-bar").width(perval + "%");
		    $(".progress-text").text(perval.toFixed(2) + "%"); // 소수점 2자리까지만
		    
		    $("#work-time").text("근무시간 : " + h + ":" + m + ":" + s);	
		  }

	  function fnClickWork(){
		  	startWorkTime = moment().format('HH:mm:ss');
 		  	$("#work-start").text("출근시간 : " + startWorkTime);			  
 		  	$("#btn-work").attr("disabled", true);
 		  	$("#btn-finish").attr("disabled", false);
 		  	
 		  	timer = setInterval(updateClock, 1000);
 		  	updateClock();
	  }
	  function fnClickFin(){
		    // 현재 시간을 HH:mm:ss 형식으로 저장
		    endWorkTime = moment().format('HH:mm:ss');

		    // 근무 시간 계산 (여기서는 단순히 예시로 사용됨)
		    workingTime = h + ":" + m + ":" + s;

		    // 화면에 퇴근 시간 표시 및 버튼 상태 변경
		    $("#work-fin").text("퇴근시간 : " + endWorkTime);			  
		    $("#btn-work").attr("disabled", false);
		    $("#btn-finish").attr("disabled", true);
		    clearInterval(timer);

		    // 현재 날짜를 가져옵니다
		    const currentDate = moment().format('YYYY-MM-DD');

		    // JSON 데이터를 생성합니다
		    const data = JSON.stringify({
		        'attInTime': startWorkTime,
		        'attOutTime': endWorkTime,
		        'attWorkTime': workingTime,
		        'createDt': currentDate,
		        'creator': empNo,
		        'modifyDt': currentDate,
		        'modifier': empNo,
		        'empNo': empNo
		    });

		    $.ajax({
		        type: 'POST',
		        url: '/crispy/registAtt',
		        contentType: 'application/json',
		        dataType: 'json',
		        data: data
		    })
			.done(function(data){
				alert("근태 저장 성공");
			})
			.fail(function(jqXHR){
				alert("근태 저장 실패");
				alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
			})
		}
	    
	  // 차트 영역
  const ctx = document.querySelector('#myChart');
  let guList = [];
  let avgSalesList = [];
  
	$.ajax({
		type:'GET',
		url: '/crispy/getGuAvgSales',
        contentType: 'application/json',
		data:'month=' + 6,
		dataType:'json'
    })
	.done(function(data){
		$(data).each(function(){
			guList.push(this.frnGu);
			avgSalesList.push(this.totalAvgSales);				
		})
		alert("매출 성공");
		      new Chart(ctx, {
	          type: 'bar',  // bar, line, pie, doughnut, radar 등등...
	          data: {
	              labels: guList,
	              datasets: [
		            {
		                type: 'bar', // 바 차트 데이터 세트
		                label: 'Bar Dataset',
		                data: avgSalesList,
		                backgroundColor: 'rgba(75, 192, 192, 0.2)',
		                borderColor: 'rgba(75, 192, 192, 1)',
		                borderWidth: 1
		            },
		            {
		                type: 'line', // 라인 차트 데이터 세트
		                label: 'Line Dataset',
		                data: avgSalesList,
		                fill: false,
		                borderColor: 'rgba(153, 102, 255, 1)'
		            }
	              ]
	          },
	          options: {
	              scales: {
	                  y: {
	                      beginAtZero: true
	                  }
	              }
	          }
	      });	  
	})
	.fail(function(jqXHR){
		alert("매출 실패");
		alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
	})    
		
