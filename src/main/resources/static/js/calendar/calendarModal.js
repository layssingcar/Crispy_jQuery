    ////////////////////////     모달 설정
      const myModal = $("#myModal");
      const startOpt = $('#start option');
      const endOpt = $('#end option');
      const radioBtnsByNotiorVac = document.getElementsByName('notice-or-vac');
      const radioBtnsByVacType = document.getElementsByName('var-elem-radio');
      const currentDate = moment().format('YYYY-MM-DD');
      let selectScheduleId;
      let idCount = 0;
      let calDate, newDate;
    	  
      myModal.on("hidden.bs.modal", function () {
        $("#form-modal")[0].reset();
        calendar.refetchEvents();
        fnSetModalDetailToggle(0);
	 	startOpt.eq(0).prop('selected', true);	// 시작시간 초기화
	 	endOpt.eq(0).prop('selected', true);		// 종료시간 초기화
	 	$("input[name='notice-or-vac']").prop("disabled", false);
      });
      
      myModal.off("show.bs.modal").on("show.bs.modal", function(){
		    // 초기 체크 상태에 따라 show/hide 설정
		    if ($("#radio-notice").is(':checked')) {
	            $("#vac-type, #vac-elem").hide();
	            $("#start, #end").show();
//            	$(".form-group-start, .form-group-end").show();
		    }
		    
		    $("input[name='notice-or-vac']").off('click').on('click', function() {		// 공지, 휴가 라디오 버튼 눌렀을 때 
		        if ($("#radio-notice").is(':checked') || $("#radio-mysche").is(':checked')) {		// 공지
		            $("#vac-type, #vac-elem").hide();
	            	$(".form-group-start, .form-group-end").show();
	            	$("#startDate, #endDate").show();
            	 	$("#start").off();									// 일정구분 라디오 초기화	
            	 	startOpt.eq(0).prop('selected', true);	// 시작시간 초기화
            	 	endOpt.eq(0).prop('selected', true);		// 종료시간 초기화
            	 	$("#end").attr("disabled", false);
		            $("#vac-all").prop("checked", true);
		            $("#start, #end").show();
            	 	
	        	} else if ($("#radio-vac").is(':checked')) {		// 휴가
		            $("#vac-type, #vac-elem").show();
		             $("#start, #end").hide();
		            
		            if($("#vac-all").is(':checked')){	// 연차
		            	$(".form-group-start, .form-group-end").show();
		            	$("#end").attr("disabled", false);
		            	$("#startDate, #endDate").show();
		            }
		            $("input[name='var-elem-radio']").off('click').on('click', function(){		// 휴가 종류 라디오 버튼 눌렀을 때 
		            	 if ($("#vac-all").is(':checked')) {	// 연차
		            		 $(".form-group-start, .form-group-end").show();
		            		 $("#startDate, #endDate").show();
		            		 $("#start, #end").hide();
		 		        } else if ($("#vac-half").is(':checked')){	// 반차
		 		        	$(".form-group-start, .form-group-end").show();
		 		        	$("#startDate, #endDate").hide();
			            	$("#end").attr("disabled", true);
			            	 $("#start, #end").show();
			            	radioControl(7);
			            	
		 		        } else if($("#vac-quat").is(':checked')){	// 반반차
		 		        	$(".form-group-start, .form-group-end").show();
		 		        	$("#startDate, #endDate").hide();
			            	$("#end").attr("disabled", true);
			            	 $("#start, #end").show();
			            	radioControl(3);
		 		        }
		            })
		        }
		    });
		});

	  function updateAMPM(num) {
    	if ((startOpt.index($("#start option:selected")) + num) >= startOpt.length) {
	        endOpt.eq(startOpt.length - 1).prop('selected', true);
    	} else {
	        endOpt.eq(startOpt.index($("#start option:selected")) + num).prop('selected', true);
    	}
	  }
      
      function radioControl(num){
      	if($("#btnradio2").is(":checked"))
    		startOpt.eq(10).prop('selected', true);
    	else if($("#btnradio1").is(":checked"))
    		startOpt.eq(0).prop('selected', true);
			
		$("#btnradio2").off('click').on('click', function() {
		    startOpt.eq(10).prop('selected', true);
		    updateAMPM(num);
		});
		$("#btnradio1").off('click').on('click', function() {
		    startOpt.eq(0).prop('selected', true);
		    updateAMPM(num);
		});
		$("#start").off('change').on('change', function() {
		    updateAMPM(num);
		});
		updateAMPM(num);	  
      }
      
      function fnRegistSchedule() {		// 일정 등록 처리 함수
      let selectScheType = $("input:radio[name=notice-or-vac]:checked").val();	// 공지,개인,연차
      let selectVacType = $("input:radio[name=var-elem-radio]:checked").val();	// 연차,반차,반반차,
      let schedule;
      let selectedstartTimeIndex = $('#start')[0].selectedIndex;
      let selectedendTimeIndex = $('#end')[0].selectedIndex;
      
      	  if(selectedendTimeIndex < selectedstartTimeIndex){
            Swal.fire({
                icon: "warning",
                title: "시작 시간과 종료 시간을 확인하세요",
                showConfirmButton: true,
                timer: 1500
            })
            return;
		  }
      
	      if($("#startDate").val() != "" && $("#endDate").val() != ""){
			if($("#startDate").val() > $("#endDate").val() != ""){
	            Swal.fire({
	                icon: "warning",
	                title: "시작 시간과 종료 시간을 확인하세요",
	                showConfirmButton: true,
	                timer: 1500
	            })
	            return;
			}
			else{
		      	startDt = $("#startDate").val();
		      	endDt = $("#endDate").val();
			}
		  }
		  else{
			calDate = moment(endDt, 'YYYY-MM-DD');
			newDate = calDate.subtract(1, 'days').format('YYYY-MM-DD');
			endDt = newDate;
		  }
      
      	if(selectScheType == 'notice' || selectScheType == 'mysche'){
	         schedule = {
				  id: (selectScheType == 'notice') ? "가맹" + crypto.randomUUID() : "개인" + crypto.randomUUID(),
	              title: $("#sch-title").val(),
	              start: startDt + "T" + $("#start option:selected").val(),
	              end : endDt + "T" + $("#end option:selected").val(),
	              backgroundColor : (selectScheType == 'notice') ? "rgba(255, 0, 0, 0.7)" : "rgba(0, 0, 255, 0.7)",
	              borderColor: (selectScheType == 'notice') ? "rgba(255, 0, 0, 0.7)" : "rgba(0, 0, 255, 0.7)"
	          };			
		}
		else if(selectScheType == 'vac') {
	         schedule = {
				  id: (selectVacType == 'all') ? "연차" + crypto.randomUUID() : ((selectVacType == 'half') ? "반차" + crypto.randomUUID() : "반반" + crypto.randomUUID()),
	              title: $("#sch-title").val(),
	              start: startDt + "T" + $("#start option:selected").val(),
	              end : endDt + "T" + $("#end option:selected").val(),
	              backgroundColor : "rgba(0, 135, 0, 0.7)",
	              borderColor: "rgba(0, 135, 0, 0.7)"
	          };
		}
        calendar.addEvent(schedule);
      	fnAddScheduleAndAnnual(schedule.id);
        calendar.unselect();
      };

      // 체크박스 관련 함수
      var allEvents;
      function fnClickCheckAll(){
    	  if ($('#allscheChecked').is(':checked')){
      	    $('.form-check-input').prop('checked', true);
      	    for(var i = 0; i < allEvents.length; i++){
				calendar.addEvent(allEvents[i]);
			}
			calendar.refetchEvents();
      	  } else{
      	    $('.form-check-input').prop('checked', false);
      	    allEvents = calendar.getEvents();
      	    calendar.removeAllEvents();
      	  }
      }
      
      function fnClickCheckSingle(){
    	  if($('#allscheChecked').is(':checked')){
    		  $('#allscheChecked').prop('checked', false);
    		  allEvents = calendar.getEvents();
  	    	  calendar.removeAllEvents();
		  }
      	  else if(!$('#allscheChecked').is(':checked'))
      	  	if($('#shopscheChecked').is(':checked') && $('#myscheChecked').is(':checked')){
				$('#allscheChecked').prop('checked', true);
			}
			
		  if($('#shopscheChecked').is(':checked')){
      	    for(var i = 0; i < allEvents.length; i++){
				if(allEvents[i].id.substring(0, 2) == "가맹"){
					calendar.addEvent(allEvents[i]);
					calendar.refetchEvents();
				}
			}
		  }
		  else{
      	    for(var i = 0; i < allEvents.length; i++){
				if(allEvents[i].id.substring(0, 2) == "가맹"){
					allEvents[i].remove();
				}
			}			
		  }
		  
		  if($('#myscheChecked').is(':checked')){
      	    for(var i = 0; i < allEvents.length; i++){
				if(allEvents[i].id.substring(0, 2) != "가맹"){
					calendar.addEvent(allEvents[i]);
					calendar.refetchEvents();
				}
			}				
		  }
  		  else{
      	    for(var i = 0; i < allEvents.length; i++){
				if(allEvents[i].id.substring(0, 2) != "가맹"){
					allEvents[i].remove();
				}
			}			
		  }			    		  
      }
      
      // 연차 ajax
      function fnDeleteAnnAjax(){
	    const data = JSON.stringify({
		        'annId': selectScheduleId,
		        'annCtNo' : 3,
		        'modifyDt': currentDate, 
		        'empNo': empNo
		    });			
		    $.ajax({
		        type: 'POST',
		        url: '/crispy/deleteAnn',
		        contentType: 'application/json',
		        dataType: 'json',
		        data: data
		    })
			.done(function(data){
					$.ajax({
						type:'GET',
						url: '/crispy/employee/getEmpNameAnn',
				        contentType: 'application/json',
						data:'empNo=' + empNo,
						dataType:'json'
				    })
				    .done(function(empData){
			            Swal.fire({
			                icon: "success",
			                title: "연차 삭제 성공! " + empData.empName + "님의 남은 연차일수는 " + empData.empAnnual + "일입니다.",
			                showConfirmButton: true,
			                timer: 2500
			            })
			            .then(()=>{
							myModal.modal('hide');
							calendar.getEventById(selectScheduleId).remove();
							calendar.refetchEvents();
						})
					})
					.fail(function(jqXHR){
			            Swal.fire({
			                icon: "warning",
			                title: "연차 삭제 실패",
			                showConfirmButton: false,
			                timer: 1500
			            })
						alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
					})    	
			})
			.fail(function(jqXHR){
	            Swal.fire({
	                icon: "warning",
	                title: "연차 삭제 실패",
	                showConfirmButton: false,
	                timer: 1500
	            })
				alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
			})  	
	  }
      
      function fnModifyAnnAjax(endTime, ctNo){
		let diffMilliseconds = moment(startDt).diff(moment(endTime));
		let duration = moment.duration(diffMilliseconds);
		let days = duration.days();
	    const data = JSON.stringify({
		        'annId': selectScheduleId,
		        'annCtNo': ctNo,
		        'annTitle': $("#sch-title").val(),
		        'annContent': $("#sch-content").val(),
		        'annTotal': days,
		        'annStartTime':  startDt + "T" + $("#start option:selected").val(),
		        'annEndTime':  endTime + "T" + $("#end option:selected").val(),
		        'modifyDt': currentDate, 
		        'empNo': empNo
		    });			
		    $.ajax({
		        type: 'POST',
		        url: '/crispy/moidfyAnn',
		        contentType: 'application/json',
		        dataType: 'json',
		        data: data
		    })
			.done(function(data){
					$.ajax({
						type:'GET',
						url: '/crispy/employee/getEmpNameAnn',
				        contentType: 'application/json',
						data:'empNo=' + empNo,
						dataType:'json'
				    })
				    .done(function(empData){
			            Swal.fire({
			                icon: "success",
			                title: "연차 수정 성공! " + empData.empName + "님의 남은 연차일수는 " + empData.empAnnual + "일입니다.",
			                showConfirmButton: true,
			                timer: 2500
			            })
			            .then(()=>{
							myModal.modal('hide');
							calendar.refetchEvents();
							location.reload();
						})
					})
					.fail(function(jqXHR){
			            Swal.fire({
			                icon: "warning",
			                title: "연차 수정 실패",
			                showConfirmButton: false,
			                timer: 1500
			            })
						alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
					})    
			})
			.fail(function(jqXHR){
	            Swal.fire({
	                icon: "warning",
	                title: "연차 수정 실패",
	                showConfirmButton: false,
	                timer: 1500
	            })
				alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
			})  
			calendar.render();		
	  }
      
      function fnAddAnnAjax(idNum, endTime, ctNo){
		let diffMilliseconds = moment(startDt).diff(moment(endTime));
		let duration = moment.duration(diffMilliseconds);
		let days = duration.days();
		
	    const data = JSON.stringify({
		        'annId': idNum,
		        'annCtNo': ctNo,
		        'annTitle': $("#sch-title").val(),
		        'annContent': $("#sch-content").val(),
		        'annTotal': days,
		        'annStartTime':  startDt + "T" + $("#start option:selected").val(),
		        'annEndTime':  endTime + "T" + $("#end option:selected").val(),
		        'createDt': currentDate,
		        'creator': empNo,
		        'modifyDt': currentDate, 
		        'modifier': empNo,
		        'empNo': empNo
		    });			
		    $.ajax({
		        type: 'POST',
		        url: '/crispy/registAnn',
		        contentType: 'application/json',
		        dataType: 'json',
		        data: data
		    })
			.done(function(data){
					$.ajax({
						type:'GET',
						url: '/crispy/employee/getEmpNameAnn',
				        contentType: 'application/json',
						data:'empNo=' + empNo,
						dataType:'json'
				    })
				    .done(function(empData){
			            Swal.fire({
			                icon: "success",
			                title: "연차 등록 성공! " + empData.empName + "님의 남은 연차일수는 " + empData.empAnnual + "일입니다.",
			                showConfirmButton: true,
			                timer: 2500
			            })
			            .then(()=>{
							myModal.modal('hide');
							calendar.refetchEvents();
						})
					})
					.fail(function(jqXHR){
			            Swal.fire({
			                icon: "warning",
			                title: "연차 저장 실패",
			                showConfirmButton: false,
			                timer: 1500
			            })
						alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
					})    		
			})
			.fail(function(jqXHR){
	            Swal.fire({
	                icon: "warning",
	                title: "연차 저장 실패",
	                showConfirmButton: false,
	                timer: 1500
	            })
				alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
			})    		
	  }

      
      function fnAddScheduleAndAnnual(idNum){
	  	let selectScheType = $("input:radio[name=notice-or-vac]:checked").val();	// 공지,개인,연차
	  	let selectVacType = $("input:radio[name=var-elem-radio]:checked").val();	// 연차,반차,반반차,
	 	
		let annCt, schDiv;
		if(selectVacType == 'all')
			annCt = 0;
		else
			annCt = (selectVacType == 'half') ? 1 : 2;
			
		if(selectScheType == 'vac'){
			if(selectVacType == 'all')
 				fnAddAnnAjax(idNum,endDt, annCt);
			else
				fnAddAnnAjax(idNum,startDt, annCt);
		}
		else if(selectScheType == 'notice' || selectScheType == 'mysche'){
			schDiv = (selectScheType == 'notice') ? 0 : 1;

		    const data = JSON.stringify({
			        'scheId': idNum,
			        'scheDiv': schDiv,
			        'scheTitle': $("#sch-title").val(),
			        'scheContent': $("#sch-content").val(),
			        'scheStartTime': startDt + "T" + $("#start option:selected").val(), 
			        'scheEndTime': endDt + "T" + $("#end option:selected").val(),
			        'createDt': currentDate,
			        'creator': empNo,
			        'modifyDt': currentDate, 
			        'modifier': empNo,
			        'scheStat': 0,
			        'empNo': empNo
			    });
			    $.ajax({
			        type: 'POST',
			        url: '/crispy/registSche',
			        contentType: 'application/json',
			        dataType: 'json',
			        data: data
			    })
				.done(function(data){
		            Swal.fire({
		                icon: "success",
		                title: "일정 저장 성공",
		                showConfirmButton: false,
		                timer: 1500
		            })
		            .then(()=>{
						myModal.modal('hide');
						calendar.refetchEvents();
					})
				})
				.fail(function(jqXHR){
		            Swal.fire({
		                icon: "warning",
		                title: "일정 저장 실패",
		                showConfirmButton: false,
		                timer: 1500
		            })
					alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
				})     			
			}
	  }
	  
	  
	  // 일정 수정 관련
	  function fnModifySchedule(){
    	$(".modal-title").text("일정 수정");
    	$("#btn-insert, #btn-modify, #btn-delete").hide();
    	$("#btn-update, #btn-cancle").show();
    	fnSetModalDetailToggle(0);		
	  }
	  
	  // 일정 삭제 관련
	  function fnDeleteSchedule(){
  		let selectScheType = $("input:radio[name=notice-or-vac]:checked").val();	// 공지,개인,연차
			
		if(selectScheType == 'vac'){
			fnDeleteAnnAjax();
		}
		else if(selectScheType == 'notice' || selectScheType == 'mysche'){
		    const data = JSON.stringify({
			        'scheId': selectScheduleId,
			        'scheStat': 1,
			        'modifyDt': currentDate, 
			        'empNo': empNo
			    });
			    $.ajax({
			        type: 'POST',
			        url: '/crispy/deleteSche',
			        contentType: 'application/json',
			        dataType: 'json',
			        data: data
			    })
				.done(function(data){
		            Swal.fire({
		                icon: "success",
		                title: "일정 삭제 성공",
		                showConfirmButton: false,
		                timer: 1500
		            })
		            .then(()=>{
						myModal.modal('hide');
						calendar.getEventById(selectScheduleId).remove();
						calendar.refetchEvents();
						location.reload();
					})
				})
				.fail(function(jqXHR){
		            Swal.fire({
		                icon: "warning",
		                title: "일정 삭제 실패",
		                showConfirmButton: false,
		                timer: 1500
		            })
					alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
				})     			
			}				
	  }
	  
	  // 일정 업데이트 관련
	  function fnUpdateSchedule(){
	  	let selectScheType = $("input:radio[name=notice-or-vac]:checked").val();	// 공지,개인,연차
	  	let selectVacType = $("input:radio[name=var-elem-radio]:checked").val();	// 연차,반차,반반차,
      	let selectedstartTimeIndex = $('#start')[0].selectedIndex;
      	let selectedendTimeIndex = $('#end')[0].selectedIndex;
      
      	  if(selectedendTimeIndex < selectedstartTimeIndex){
            Swal.fire({
                icon: "warning",
                title: "시작 시간과 종료 시간을 확인하세요",
                showConfirmButton: true,
                timer: 1500
            })
            return;
		  }
	 	
		let annCt, schDiv;
		if(selectVacType == 'all')
			annCt = 0;
		else
			annCt = (selectVacType == 'half') ? 1 : 2;
			
	    if($("#startDate").val() != "" && $("#endDate").val() != ""){
			if($("#startDate").val() > $("#endDate").val() != ""){
	            Swal.fire({
	                icon: "warning",
	                title: "시작 시간과 종료 시간을 확인하세요",
	                showConfirmButton: true,
	                timer: 1500
	            })
	            return;
			}
			else{
		      	startDt = $("#startDate").val();
		      	endDt = $("#endDate").val();
			}
		}			
			
		if(selectScheType == 'vac'){
			if(selectVacType == 'all')
 				fnModifyAnnAjax(endDt, annCt);
			else
				fnModifyAnnAjax(startDt, annCt);
		}
		else if(selectScheType == 'notice' || selectScheType == 'mysche'){
			schDiv = (selectScheType == 'notice') ? 0 : 1;
		    const data = JSON.stringify({
			        'scheId': selectScheduleId,
			        'scheDiv': schDiv,
			        'scheTitle': $("#sch-title").val(),
			        'scheContent': $("#sch-content").val(),
			        'scheStartTime': startDt + "T" + $("#start option:selected").val(), 
			        'scheEndTime': endDt + "T" + $("#end option:selected").val(),
			        'modifyDt': currentDate, 
			        'empNo': empNo
			    });
			    $.ajax({
			        type: 'POST',
			        url: '/crispy/modifySche',
			        contentType: 'application/json',
			        dataType: 'json',
			        data: data
			    })
				.done(function(data){
		            Swal.fire({
		                icon: "success",
		                title: "일정 수정 성공",
		                showConfirmButton: false,
		                timer: 1500
		            })
		            .then(()=>{
				      	let event = calendar.getEventById(selectScheduleId);
						calDate = moment(endDt, 'YYYY-MM-DD');
						newDate = calDate.add(1, 'days').format('YYYY-MM-DD');
						endDt = newDate;
			      	    event.setEnd(endDt);
			      	    
						myModal.modal('hide');
						calendar.refetchEvents();
						location.reload();
					})
				})
				.fail(function(jqXHR){
		            Swal.fire({
		                icon: "warning",
		                title: "일정 수정 실패",
		                showConfirmButton: false,
		                timer: 1500
		            })
					alert(jqXHR.statusText + '(' + jqXHR.status + ')');  					
				})     			
			}
	  }
	  
  	  // 일정 업데이트 취소
	  function fnCancleUpdateSchedule(){
		 myModal.modal('hide');
	  }
	  