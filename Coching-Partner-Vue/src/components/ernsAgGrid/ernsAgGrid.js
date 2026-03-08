export default {
  data() {
    return {
      grids: new Map() // 여러 개의 그리드를 저장하는 Map
    };
  },
  methods: {
    /**
		 * Grid 인스턴스 등록 및 초기 데이터 저장
		 * @param {String} gridId 
		 * @param {Object} params AgGrid Params
		 * @param {*} gridOptions 
		 */
    ernsAgGrid_registerGrid(gridId, params, gridOptions) {
			const _vm = this;
			// console.debug(params);

			const gridContext = _vm.grids.get(gridId) || {};
			gridContext.gridOptions = gridOptions;
			gridContext.originalData = [];	// 원본 데이터 저장
			gridContext.modifiedCells = {};	// 변경된 셀 저장
			_vm.grids.set(gridId, {
				gridOptions: gridOptions,
				api: params.api,
				originalData: [],	
				modifiedCells: {} 
			});
    },

		/**
		 * 그리드 context 리턴
		 * @param {String} gridId 
		 * @returns 
		 */
		ernsAgGrid_getGridContext(gridId){
			const _vm = this;
      if (!_vm.grids.has(gridId)) {
				return null;
			} 

			const gridContext = _vm.grids.get(gridId);
			return gridContext;
		},

		/**
		 * 그리드 초기 데이터 저장
		 * @param {String} gridId 
		 * @param {Array} rowData 
		 */
		ernsAgGrid_resetOriginalData(gridId, rowData) {
			const _vm = this;
      const gridContext = _vm.ernsAgGrid_getGridContext(gridId);
			if(!gridContext) {
				return;
			}

			const rawData = JSON.parse(JSON.stringify(rowData));
			const fncGetRowId = gridContext.gridOptions.getRowId;
			gridContext.originalData = rawData.map(item=>{
				return {
					...item,
					getRowId:()=>{
						const params = {data:item};
						return fncGetRowId(params);
					}
				}
			});
			gridContext.modifiedCells = {};
			gridContext.gridOptions.isEditData = false;
			//console.debug(gridContext.originalData);
    },

		/**
		 * 그리드 초기 데이터 로드
		 * @param {String} gridId 
		 */
		ernsAgGrid_getOriginalData(gridId){
			const _vm = this;

			const gridContext = _vm.ernsAgGrid_getGridContext(gridId);
			if(!gridContext) {
				return null;
			}
			
			return gridContext.originalData;
		},

    /**
     * 🌟 셀 변경 시 수정된 데이터를 저장
     */
    ernsAgGrid_trackCellChanges(gridId, params) {
			const _vm = this;

			const rowNode = params.node;
			const rowId = params.node.id;			
			const gridContext = _vm.ernsAgGrid_getGridContext(gridId);
			if (!gridContext || !rowId) {
				return;
			}

			if('_isEditRow' == params.colDef.field){
				return;
			}

			if (!gridContext.modifiedCells[rowId]) {
				gridContext.modifiedCells[rowId] = {};
			} 

			const orgRawData = gridContext.originalData.find(orgRow=>rowId == orgRow.getRowId()) || {};
			if(orgRawData[params.colDef.field] == params.newValue){
				delete gridContext.modifiedCells[rowId][params.colDef.field];
			}else{
				gridContext.modifiedCells[rowId][params.colDef.field] = params.newValue;
			}

			const isEditedRow = Object.values(gridContext.modifiedCells[rowId]).length > 0;
			if(!isEditedRow){
				//수정된 항목이 하나도 없으면 
				delete gridContext.modifiedCells[rowId];				
			}
			rowNode.setDataValue("_isEditRow", isEditedRow); //수정 더티 플래그 on
			params.api.refreshCells({ rowNodes: [params.node], columns: [params.column.colId], force: true });
			
      console.log(`[${gridId}] 수정된 셀 목록:`, gridContext.modifiedCells);

			gridContext.gridOptions.isEditData = _vm.ernsAgGrid_isEditData(gridId);

    },

		/**
     * 🌟 특정 그리드의 편집된 셀 데이터 조회
     */
    ernsAgGrid_isEditData(gridId) {
			const _vm = this;

			const gridContext = _vm.ernsAgGrid_getGridContext(gridId);
			if (!gridContext) {
				return false;
			}

			const modifiedCells = gridContext?.modifiedCells || {};
			return Object.values(modifiedCells).length > 0;
    },

    /**
     * 🌟 특정 그리드의 편집된 셀 데이터 조회
     */
    ernsAgGrid_getModifiedCells(gridId, rowId, fieldName) {
			const _vm = this;

			const gridContext = _vm.ernsAgGrid_getGridContext(gridId);
			if (!gridContext) {
				return;
			}

			if(!rowId){
				return gridContext?.modifiedCells || null;
			} 

			if(!fieldName){
				return gridContext?.modifiedCells[rowId] || null;
			}
			
			return gridContext?.modifiedCells?.[rowId]?.[fieldName] || null;
    },

    /**
     * 🌟 특정 그리드의 모든 데이터를 원본으로 복구
     */
    ernsAgGrid_resetToOriginal(gridId) {
			const _vm = this;
      
			const gridContext = _vm.ernsAgGrid_getGridContext(gridId);
			if(!gridContext){
				return;
			}

			if(!gridContext.originalData) {
				return;
			}

			gridContext.api.updateGridOptions(
				{ rowData: JSON.parse(JSON.stringify(gridContext.originalData)) }
			);
			gridContext.modifiedCells = {};
			console.log(`[${gridId}] 원본 데이터로 복구됨!`);
    },

    /**
     * 🌟 특정 그리드의 특정 행(Row)만 원본 데이터로 복구
     */
    ernsAgGrid_restoreRow(gridId, rowId) {
			const _vm = this;

      const gridContext = _vm.ernsAgGrid_getGridContext(gridId);
			if(!gridContext) {
				return;
			}

			if(!gridContext.originalData[rowId]) {
				return;
			}

			const updatedRowData = gridContext.api.getRowData().map(row =>
				row._rowId === rowId ? { ...gridContext.originalData[rowId] } : row
			);
			gridContext.api.updateGridOptions(
				{ rowData: updatedRowData }
			);
			delete gridContext.modifiedCells[rowId];
			console.log(`[${gridId}] Row ID ${rowId} 복구 완료!`);
    },

		//rowId 로 RowNode 가져오기
		ernsAgGrid_getRowNode(gridRefId, rowId) {
			const _vm = this;

			const gridRef = _vm.$refs[gridRefId];
      const rowNode = gridRef.api.getRowNode(rowId); // 특정 rowId로 행 찾기
      return rowNode;
    },

		//rowId 로 Row 삭제
		ernsAgGrid_deleteRow(gridRefId, rowId) {
			const _vm = this;

			const gridRef = _vm.$refs[gridRefId];
      const rowNode = _vm.ernsAgGrid_getRowNode(gridRefId, rowId);
      if (rowNode) {
        gridRef.api.applyTransaction({ remove: [rowNode.data] }); // 삭제
      } else {
        //alert(`ID ${rowId}를 찾을 수 없습니다.`);
      }
    },

		/**
		 * 편집된 셀의 정보를 출력
		 * @param {*} gridId 
		 */
		ernsAgGrid_showModifiedCells(gridId) {
			const _vm = this
      console.log(`[${gridId}] 편집된 셀 정보:`, _vm.ernsAgGrid_getModifiedCells(gridId));
    },

		//////////////////////////////////////////////////////////////////////////////////////////
		// Validation 
		/**
		 * 입력값이 공백인지 확인
		 * @param {*} params 
		 * @returns 
		 */
		ernsAgGrid_validateNotEmpty(params, message) {
			const _vm = this;
      const newValue = (params.newValue || "").trim(); // 화이트 스페이스 제거
      if (!newValue) {
        _vm.alertError(message || "값을 입력해야 합니다.");
        return false; // 값 설정 거부
      }
      params.data[params.column.colId] = newValue; // 해당 컬럼에 값 저장
      return true; // 정상적으로 반영
    },
  }
};
