import {
    PROF_TEST_REQUESTED,
    PROF_TEST,
    TEST_RESULT_REQUESTED,
    TEST_RESULT,
	PROF_TEST_CLEAR,
} from '../actions';

import {get_prof_test as get_prof_test_api} from '../../api/test';
import {get_test_result as get_test_result_api} from '../../api/test';

export const get_prof_test = (token, selectedLanguage) => {
	return dispatch => {
		dispatch({
			type : PROF_TEST_REQUESTED
		});
		get_prof_test_api(token, selectedLanguage)
			.then(profTest => {
				dispatch({
                    type: PROF_TEST,
                    profTest: profTest,
				});
			});
	}
}

export const get_test_result = (token, testId, answers) => {
	return dispatch => {
		dispatch({
			type : TEST_RESULT_REQUESTED
		});

		get_test_result_api(token, testId, answers)
			.then(response => {
				dispatch({
                    type: TEST_RESULT,
                    testResult: response,
				});
			});
	}
}

export const clear_prof_test = () => {
	return dispatch => {
		dispatch({
			type : PROF_TEST_CLEAR,
		});
	}
}
