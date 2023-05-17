import React, { useState } from 'react';
import {Navigate} from 'react-router-dom';
import './Styles/SearchEngine.css';
import SearchBar from './SearchBar';

function SearchEngine(props) {
    const [searchText, setSearchText] = useState('');
    const [shouldNavigate, setShouldNavigate] = useState(false);
    const [suggestFlag, setSuggestFlag] = useState(true);
    const handleSearch = (searchText) =>{
        setShouldNavigate(true);
        props.handleSearch(searchText);
        console.log(searchText);
    }

    const getResults = (text) => {
        props.getResults(text);
    }

    return (
        <div className='searchPage'>
            <div>
                <h1 className='searchEngine'>Search Engine</h1>
            </div>
            <SearchBar
            searchText={''}
            handleSearch={handleSearch}
            suggestionsFlag={suggestFlag}
            setFlag={setSuggestFlag}
            getResults={getResults}/>
            {shouldNavigate && <Navigate to='/Results'/>}
        </div>
    );
}

export default SearchEngine;