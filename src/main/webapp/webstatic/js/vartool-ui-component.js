function FileComponent(opt) {
	this.init(opt);
	return this;
}

function getAcceptExtensions(exts) {
	if (exts == 'all') {
		return '*.*';
	}

	return '.' + VARTOOL.str.allTrim(exts).split(',').join(',.');
}

FileComponent.prototype = {
	_$fileObj: {}
	, opts: {}
	, btnClassName: ''
	, defaultParams: {}
	, init: function (opt) {
		var _this = this;

		var strHtm = [];
		strHtm.push('	<ul class="file-row">');
		strHtm.push('	  <li class="file-action-btn">');
		strHtm.push('		<button type="button" data-dz-remove class="btn file-remove" title="Remove">');
		strHtm.push('			<i class="fa fa-trash"></i>');
		strHtm.push('		</button>');

		if (opt.useDownloadBtn === true) {
			strHtm.push('		<button type="button" class="btn file-download" title="Download">');
			strHtm.push('			<i class="fa fa-download"></i>');
			strHtm.push('		</button>');
		}

		strHtm.push('	  </li>');
		strHtm.push('	  <li class="file-info">');
		strHtm.push('		<span class="file-name text-ellipsis" data-dz-name></span> <span class="file-size" data-dz-size></span>');
		strHtm.push('	  </li>');
		strHtm.push('	  <li class="file-progress">');
		strHtm.push('		<div class="error-view-area"><strong class="error text-danger" data-dz-errormessage></strong></div>');
		strHtm.push('		<div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0">');
		strHtm.push('			<div class="progress-bar progress-bar-success" style="width:0%;" data-dz-uploadprogress></div>');
		strHtm.push('		</div>');
		strHtm.push('	  </li>');
		strHtm.push('	</ul>');

		opt = VARTOOL.util.objectMerge({
			mode: 'basic' // basic, template
			, extensions: ''
			, files: []
			, successAfterReset: true
			, callback: {
				fail: function (file, resp) { }
				, complete: function (file, resp) { }
				, addFile: function (file) { }
				, removeFile: function (file) { }
				, download: function (file) { }
			}
			, options: {}
		}, opt);

		var dropzoneOpt = VARTOOL.util.objectMerge({
			url: "http://www.vartool.com", // upload url
			thumbnailWidth: 50,
			thumbnailHeight: 50,
			parallelUploads: 20,
			uploadMultiple: true,
			maxFilesize: VARTOOL.getFileMaxUploadSize(),
			autoQueue: false,
			previewTemplate: opt.previewTemplate || strHtm.join(''),
			clickable: false,
			previewsContainer: ''
		}, opt.options);

		_this.defaultParams = dropzoneOpt.params;

		if (opt.extensions != '') {
			dropzoneOpt.acceptedFiles = getAcceptExtensions(opt.extensions);
		}

		if (opt.mode == 'template') {

		} else {
			var btnClassName = 'b-' + VARTOOL.generateUUID();
			this._btnClear(opt)

			if (opt.btnEnabled !== false) {
				var btnClass = btnClassName;
				this.btnClassName = 'wrapper-' + btnClass;

				var btnHtml = '<div class="file-add-btn-area wrapper-' + btnClass + '""><button type="button" class="file-add-btn ' + btnClass + '">파일찾기</button></div>';
				if (opt.btn == 'top') {
					$(opt.el).before(btnHtml);
				} else {
					$(opt.el).after(btnHtml);
				}

				btnClass = '.' + btnClass;
				if (VARTOOL.isString(dropzoneOpt.clickable)) {
					dropzoneOpt.clickable = dropzoneOpt.clickable + ' ' + btnClass;
				} else if (VARTOOL.isArray(dropzoneOpt.clickable)) {
					dropzoneOpt.clickable.push(btnClass);
				} else {
					dropzoneOpt.clickable = btnClass;
				}
			}
		}

		if (dropzoneOpt.previewsContainer != '') {
			$(dropzoneOpt.previewsContainer).empty();
		}

		var dropzone = new Dropzone(opt.el, dropzoneOpt);

		if (opt.useDownloadBtn === true) {
			var isDownload = $.isFunction(opt.callback.download);

			$(dropzoneOpt.previewsContainer).off('click.download')
			$(dropzoneOpt.previewsContainer).on('click.download', '.file-download', function (e) {
				var fileRowEle = $(this).closest('.file-row');
				var fileIdx = fileRowEle.index();

				if (isDownload) {
					opt.callback.download(dropzone.files[fileIdx])
				}
			})
		}

		var isDuplCallback = $.isFunction(opt.callback.duplicateFile);
		dropzone.on("addedfile", function (file) {
			var addFlag = true;

			if (opt.duplicateIgnore === true) {
				var len = this.files.length;
				if (file.status == 'added' && len > 0) {
					for (var i = 0; i < len - 1; i++) {
						if (this.files[i].name === file.name) {
							this.removeFile(file);
							addFlag = false;
							if (isDuplCallback) {
								opt.callback.duplicateFile(file);
							}
						}
					}
				}
			}

			if (addFlag) {
				opt.callback.addFile(file);
				file.previewElement.querySelector('.file-name').title = file.name;
			}
		});

		var isCallback = $.isFunction(opt.callback.removeFile);
		dropzone.on("removedfile", function (file) {
			if (isCallback) {
				opt.callback.removeFile(file);
			}
		});

		dropzone.on("maxfilesexceeded", function (file) {
			this.removeFile(file);
		});

		if (dropzoneOpt.uploadMultiple === true) {
			dropzone.on('successmultiple', function (file, resp) {
				return _this._fileUploadSuccess(opt, file, resp);
			});
			dropzone.on('errormultiple', function (file, resp) {
				opt.callback.fail(file);
			});
		} else {
			dropzone.on('success', function (file, resp) {
				return _this._fileUploadSuccess(opt, file, resp);
			});

			dropzone.on('error', function (file, resp) {
				opt.callback.fail(file);
			});
		}

		if (VARTOOL.isArray(opt.files)) {
			var fileLen = opt.files.length;
			for (var i = 0; i < fileLen; i++) {
				var fileItem = opt.files[i];
				fileItem.accepted = true;
				dropzone.emit('addedfile', fileItem);
				dropzone.emit('thumbnail', fileItem);
				dropzone.emit('complete', fileItem);
				dropzone.files.push(fileItem);
			}
		};
		this._$fileObj = dropzone
		return this;
	}
	, _getFileObj: function () {
		return this._$fileObj;
	}
	, _getFiles: function () {
		return this._$fileObj.files;
	}
	, _setExtensions: function (exts) {
		this._$fileObj.hiddenFileInput.setAttribute("accept", getAcceptExtensions(exts));
		this._$fileObj.options.acceptedFiles = getAcceptExtensions(exts);
	}
	, _clearFiles: function () {
		this._$fileObj.removeAllFiles(true);
	}
	, _fileUploadSuccess: function (opt, file, resp) {
		if (VARTOOL.reqCheck(resp)) {
			if (opt.successAfterReset === true) {
				this._clearFiles(true);
			}
			this._$fileObj.removeFile(file);
			opt.callback.complete(file, resp);
		} else {
			this._$fileObj.emit('error', file, resp.message);
		}
		return false;
	}
	, getRejectedFiles: function () {
		return this._$fileObj.getRejectedFiles();
	}
	, getAddFiles: function () {
		return this._$fileObj.getFilesWithStatus(Dropzone.ADDED);
	}
	, save: function (param) {
		var dropzoneObj = this._$fileObj;
		dropzoneObj.options.params = function () {
			return VARTOOL.util.objectMerge({}, _this.defaultParams, param);
		};

		if (dropzoneObj.files.length > 0) {
			dropzoneObj.enqueueFiles(dropzoneObj.getFilesWithStatus(Dropzone.ADDED));
			//dropzoneObj.processQueue();
		} else {
			var blob = new Blob();
			blob.upload = { 'chunked': dropzoneObj.defaultOptions.chunking };
			dropzoneObj.uploadFile(blob);
		}
	}
}


var VARTOOLUI = { file: {} };
VARTOOLUI.file = {
	allObj: {}
	, create: function (el, opt) {
		if (typeof this.allObj[el] === 'undefined') {
			if (typeof opt['el'] === 'undefined') {
				opt['el'] = el;
			}
			this.allObj[el] = new FileComponent(opt);
		}

		return this.allObj[el];
	}
	, forElement: function (el) {
		return this.allObj[el];
	}


};