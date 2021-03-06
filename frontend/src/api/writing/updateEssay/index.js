import parameters from '../../parameters'
import axios from 'axios';

// https://chrome.google.com/webstore/detail/access-control-allow-cred/hmcjjmkppmkpobeokkhgkecjlaobjldi should be added to chrome

export const respond_to_essay = async (token, status, id) => {

    let essay = await axios
        .patch(parameters.apiUrl+'/essay/' + id + '/',
            {
                status,
            },
            {
                headers: {
                    'Content-Type':'application/json',
                    'Authorization': 'Token '+token,
                },
                timeout: 10000,
            }
        )
        .then(response => response.data)
        .catch(err => {
            return {
                message: 'Error',
            };
        });
    
    return essay;
}

export const change_reviewer_of_essay = async (token, reviewer, id) => {
    
    console.log(token);
    console.log(reviewer);
    console.log(id);

    let essay = await axios
        .patch(parameters.apiUrl+'/essay/' + id + '/',
            {
                reviewer,
            },
            {
                headers: {
                    'Content-Type':'application/json',
                    'Authorization': 'Token '+token,
                },
                timeout: 10000,
            }
        )
        .then(response => response.data)
        .catch(err => {
            return {
                message: 'Error',
            };
        });
    
    return essay;
}