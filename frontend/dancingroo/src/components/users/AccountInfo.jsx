import React, {useState} from "react";
// import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import {FaEdit} from 'react-icons/fa';
import { useSelector } from "react-redux";
const Wrapper = styled.div`
    display: flex;
    flex-direction: column;
`;
const Title = styled.div`
    display: flex;
    align-items: center;
    font-size: 1.6rem;
    font-weight: 600;
    margin-bottom: 0.1rem;
    cursor: pointer;
`;
const Experience = styled.div`
    width: 12rem;
    height: 1.5rem;
    border: 0.1rem solid #F05475;
    border-radius: 1rem;
`;
const ExperiencePercentage = styled.div`
    width:${props=>props.gauge}%;
    height: 1.5rem;
    background-color: #F05475;
    border-radius: 0.5rem;
`;

function AccountInfo(props) {
    // const navigate = useNavigate();
    const familyname = useSelector(state=>state.userState.familyname)
    const [experiencePercentage, setExperiencePercentage] = useState(50);

    return (
        <Wrapper>
            <Title>{familyname} <FaEdit size={18} color="#F05475"/></Title>
            <Experience>
                <ExperiencePercentage gauge={experiencePercentage}/>
            </Experience>
        </Wrapper>
    );
}

export default AccountInfo;
